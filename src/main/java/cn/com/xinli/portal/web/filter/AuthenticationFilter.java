package cn.com.xinli.portal.web.filter;

import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import cn.com.xinli.portal.web.auth.event.AuthenticationFailureEvent;
import cn.com.xinli.portal.web.auth.event.AuthenticationSuccessEvent;
import cn.com.xinli.portal.web.rest.RestRequest;
import cn.com.xinli.portal.web.rest.RestRequestSupport;
import cn.com.xinli.portal.web.util.CredentialsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Rest Authentication Filter.
 *
 * <p>This filter was created to verify REST client credentials
 * before server start to process incoming requests. It's a part of spring-security
 * framework. This filter was place in the spring-web-security filter chain
 * before spring's anonymous filter.
 *
 * <p>This filter works only when client try to accept protected APIs,
 * which is identified by "requires_auth" attribute.
 *
 * <p>All exceptions thrown by this class are extends from spring-security framework's
 * {@link AuthenticationException}. When exceptions occurs, pre-defined
 * {@link AuthenticationEntryPoint} will try to commence, it respond a
 * error message in JSON back to remote client.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Component
@Order(20)
public class AuthenticationFilter extends AbstractRestFilter implements ApplicationEventPublisherAware {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    /** Should continue filter chain if filter failed. */
    private boolean continueFilterChainOnUnsuccessful = false;

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
        Assert.notNull(authenticationManager);
        Assert.notNull(authenticationEntryPoint);
        setContinueFilterChainOnUnsuccessful(false);
    }

    /**
     * Set filter whether continue FilterChain On Unsuccessful.
     * @param continueFilterChainOnUnsuccessful false will throw an {@link AuthenticationException}
     */
    public void setContinueFilterChainOnUnsuccessful(boolean continueFilterChainOnUnsuccessful) {
        this.continueFilterChainOnUnsuccessful = continueFilterChainOnUnsuccessful;
    }

    /**
     * Handle successful authentication.
     * @param authentication authentication authenticated.
     */
    private void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Authentication authentication) {
        if (logger.isTraceEnabled()) {
            logger.trace("Authentication success: {}", authentication);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(
                    new AuthenticationSuccessEvent(request, response, authentication));
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
        request.setAttribute(AccessAuthentication.FAILED_AUTHENTICATION, authentication);

        SecurityContextHolder.clearContext();
        if (logger.isTraceEnabled()) {
            logger.trace("* Cleared security context due to exception, {}", failed.getMessage());
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
        if (matches(request)) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} ==> http://localhost{} Authentication filter checking...",
                        request.getMethod(), request.getRequestURI());
            }

            Optional<HttpDigestCredentials> opt = CredentialsUtils.getCredentials(request);
            if (opt.isPresent()) {
                /*
                 * Only start authentication when request access protected resources
                 * and there's credentials inside the request.
                 */
                AccessAuthentication authentication = AccessAuthentication.empty();
                try {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Checking secure context authentication: {}",
                                SecurityContextHolder.getContext().getAuthentication());
                    }

                    HttpDigestCredentials credentials = opt.get();
                    String principal = credentials.getAttribute(HttpDigestCredentials.CLIENT_ID);
                    if (principal == null) {
                        throw new BadCredentialsException("principal not found.");
                    }

                    authentication = new AccessAuthentication(principal, credentials);
                    authentication.setDetails(createRestRequest(request, credentials));

                    Authentication result = authenticationManager.authenticate(authentication);
                    SecurityContextHolder.getContext().setAuthentication(result);
                    successfulAuthentication(request, response, result);
                    if (logger.isTraceEnabled()) {
                        logger.trace("authorities: {}.", result.getAuthorities());
                    }
                } catch (AuthenticationException e) {
                    unsuccessfulAuthentication(request, response, authentication, e);
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
