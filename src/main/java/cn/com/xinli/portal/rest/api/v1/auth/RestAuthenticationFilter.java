package cn.com.xinli.portal.rest.api.v1.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.api.RestAuthenticationFailureEvent;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeAuthentication;
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
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Rest Authentication Filter.
 *
 * This filter extends from spring-security {@link OncePerRequestFilter}.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class RestAuthenticationFilter extends OncePerRequestFilter {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthenticationFilter.class);

    /** Should continue filter chain if filter failed. */
    private boolean continueFilterChainOnUnsuccessful = false;

    /** Inclusive path array. */
    private String[] filterPathMatches = null;

    @Autowired
    private AuthorizationServer authorizationServer;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(filterPathMatches);
        Assert.notNull(authorizationServer);
    }

    public void setFilterPathMatches(String[] filterPathMatches) {
        this.filterPathMatches = filterPathMatches;
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
        return Stream.of(filterPathMatches).anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (log.isDebugEnabled()) {
            log.debug("Checking secure context token: " +
                    SecurityContextHolder.getContext().getAuthentication());
        }

        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
        } else {
            try {
                authorizationServer.authenticate(request, response);
                filterChain.doFilter(request, response);
            } catch (AuthenticationException e) {
                authorizationServer.unsuccessfulAuthentication(request, response, e);
                if (continueFilterChainOnUnsuccessful) {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }
}
