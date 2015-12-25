package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.rest.CredentialsUtil;
import cn.com.xinli.portal.rest.RestRequestSupport;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Rest Authentication Filter.
 *
 * This filter extends from spring-security {@link OncePerRequestFilter}.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class AuthenticationFilter extends OncePerRequestFilter implements ApplicationEventPublisherAware {
    /** Log. */
    private static final Log log = LogFactory.getLog(AuthenticationFilter.class);

    /** Should continue filter chain if filter failed. */
    private boolean continueFilterChainOnUnsuccessful = false;

    /** Inclusive path array. */
    private final List<String> filterPathMatches = new ArrayList<>();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CertificateService certificateService;

    /** Application event publisher. */
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }


    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(filterPathMatches);
        Assert.notNull(authenticationManager);
        Assert.notNull(authenticationEntryPoint);
    }

    public void setFilterPathMatches(Collection<String> filterPathMatches) {
        this.filterPathMatches.addAll(filterPathMatches);
    }

    /**
     * Set filter whether continue FilterChain On Unsuccessful.
     * @param continueFilterChainOnUnsuccessful false will throw an {@link AuthenticationException}
     */
    public void setContinueFilterChainOnUnsuccessful(boolean continueFilterChainOnUnsuccessful) {
        this.continueFilterChainOnUnsuccessful = continueFilterChainOnUnsuccessful;
    }

    private boolean requiresAuthentication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return filterPathMatches.stream().anyMatch(uri::startsWith);
    }

    private void successfulAuthentication(Authentication authentication) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication success: " + authentication);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(
                    new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()));
        }
    }

    private void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication,
                                            AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Cleared security context due to exception:" + failed.toString());
        }

        request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", failed);

        applicationEventPublisher.publishEvent(
                new AuthenticationFailureEvent(request, response, authentication, failed));
    }

    /**
     * Verify request by checking its integrity and checking if
     * request was originated in allowed time range.
     *
     * @param request http request.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifyRequest(HttpServletRequest request, HttpDigestCredentials credentials) {
        long now = System.currentTimeMillis() / 1000L;
        String ts = credentials.getParameter(HttpDigestCredentials.TIMESTAMP);
        long timestamp = StringUtils.isEmpty(ts) ? -1L : Long.parseLong(ts);
        long diff = Math.abs(now - timestamp);

        if (diff > SecurityConfiguration.MAX_TIME_DIFF) {
            throw new BadCredentialsException("Way too inaccurate timestamp.");
        }

        /*
         * Create a REST request from incoming request and then sign it,
         * so that we can verify incoming request with signed one.
         */
        RestRequestSupport support = new RestRequestSupport();
        request.getParameterMap().forEach((s, strings) -> {
            for (String string : strings) {
                support.setParameter(s, string);
            }
        });
        credentials.getParameters().forEach((s, o) -> support.getCredentials().setParameter(s, o));

        /* Retrieve shared secret. */
        String clientId = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
        Certificate certificate = certificateService.loadCertificate(clientId);

        if (log.isDebugEnabled()) {
            log.debug("> certificate loaded: " + certificate);
        }

        /* Sign request and compare with incoming one. */
        support.sign(request.getMethod(), request.getRequestURI(), certificate.getSharedSecret());

        String signature = credentials.getParameter(HttpDigestCredentials.SIGNATURE);
        String signedSignature = support.getCredentials().getParameter(HttpDigestCredentials.SIGNATURE);

        if (signature == null) {
            throw new BadCredentialsException("Missing signature.");
        }

        if (log.isDebugEnabled()) {
            log.debug("> Comparing signature, calculated: {" + signedSignature +
                    "}, original: {" + signature + "}");
        }

        if (!signedSignature.equals(signature)) {
            throw new BadCredentialsException("Signature verify failed.");
        }

        log.debug("> Request signature verified.");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request)) {
            Optional<HttpDigestCredentials> opt = CredentialsUtil.getCredentials(request);
            if (opt.isPresent()) {
                /*
                 * Only start authentication when request access protected resources
                 * and there's credentials inside the request.
                 */
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Checking secure context token: " +
                                SecurityContextHolder.getContext().getAuthentication());
                    }

                    HttpDigestCredentials credentials = opt.get();
                    String principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
                    if (principal == null) {
                        throw new BadCredentialsException("principal not found.");
                    }

                    /* Verify incoming request before authenticate it. */
                    verifyRequest(request, credentials);

                    AccessAuthentication authentication = new AccessAuthentication(principal, credentials);

                    Authentication result = authenticationManager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(result);
                    successfulAuthentication(result);
                    if (log.isDebugEnabled()) {
                        log.debug("> authorities: " + result.getAuthorities());
                    }
                } catch (AuthenticationException e) {
                    unsuccessfulAuthentication(request, response, AbstractAuthentication.empty(), e);
                    if (!continueFilterChainOnUnsuccessful) {
                        authenticationEntryPoint.commence(request, response, e);
                        return; // skip filter china on unsuccessful.
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
