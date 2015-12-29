package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.CredentialsUtil;
import cn.com.xinli.portal.rest.RestRequest;
import cn.com.xinli.portal.rest.RestRequestSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    /** Should continue filter chain if filter failed. */
    private boolean continueFilterChainOnUnsuccessful = false;

    /** Inclusive path array. */
    private final List<String> filterPathMatches = new ArrayList<>();

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

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

    /**
     * Check if target resource need authentication.
     * @param request request.
     * @return true if authentiation needed.
     */
    private boolean requiresAuthentication(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return filterPathMatches.stream().anyMatch(uri::startsWith);
    }

    /**
     * Handle successful authentication.
     * @param authentication authentication authenticated.
     */
    private void successfulAuthentication(Authentication authentication) {
        if (logger.isDebugEnabled()) {
            logger.debug("Authentication success: {}", authentication);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(
                    new InteractiveAuthenticationSuccessEvent(authentication, this.getClass()));
        }
    }

    /**
     * Handle unsuccessful authentication.
     * @param request http request.
     * @param response http response.
     * @param authentication authentication failed to authenticate.
     * @param failed Authentication exception caused failure.
     */
    private void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication,
                                            AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        if (logger.isDebugEnabled()) {
            logger.error("* Cleared security context due to exception, {}", failed.getMessage());
        }

        request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", failed);

        applicationEventPublisher.publishEvent(
                new AuthenticationFailureEvent(request, response, authentication, failed));
    }

    /**
     * Create REST request.
     * @param request HTTP request.
     * @param credentials credentials.
     * @return REST request.
     */
    private RestRequest createRestRequest(HttpServletRequest request, HttpDigestCredentials credentials) {
        RestRequestSupport rest = new RestRequestSupport(request.getMethod(), request.getRequestURI());
        request.getParameterMap().forEach((s, strings) -> {
            for (String string : strings) {
                rest.setParameter(s, string);
            }
        });
        rest.getCredentials().copy(credentials);
        return rest;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("==> {} {} Authentication filter checking...",
                        request.getMethod(), request.getRequestURI());
            }

            Optional<HttpDigestCredentials> opt = CredentialsUtil.getCredentials(request);
            if (opt.isPresent()) {
                /*
                 * Only start authentication when request access protected resources
                 * and there's credentials inside the request.
                 */
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Checking secure context token: {}",
                                SecurityContextHolder.getContext().getAuthentication());
                    }

                    HttpDigestCredentials credentials = opt.get();
                    String principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
                    if (principal == null) {
                        throw new BadCredentialsException("principal not found.");
                    }

                    AccessAuthentication authentication = new AccessAuthentication(principal, credentials);
                    authentication.setDetails(createRestRequest(request, credentials));

                    Authentication result = authenticationManager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(result);
                    successfulAuthentication(result);
                    if (logger.isDebugEnabled()) {
                        logger.debug("> authorities: {}.", result.getAuthorities());
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
