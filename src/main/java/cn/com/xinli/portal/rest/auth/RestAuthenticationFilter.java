package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.RestResponse;
import cn.com.xinli.portal.rest.bean.Failure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    private final List<String> filterPathMatches = new ArrayList<>();

    @Autowired
    private AuthorizationServer authorizationServer;

    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();
        Assert.notNull(filterPathMatches);
        Assert.notNull(authorizationServer);
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!requiresAuthentication(request)) {
            filterChain.doFilter(request, response);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Checking secure context token: " +
                        SecurityContextHolder.getContext().getAuthentication());
            }

            Authentication result = AbstractRestAuthentication.empty();
            try {
                result = authorizationServer.authenticate(request, response);
                filterChain.doFilter(request, response);
            } catch (AuthenticationException e) {
                authorizationServer.unsuccessfulAuthentication(request, response, result, e);
                if (continueFilterChainOnUnsuccessful) {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Failure> handleAuthenticationException(AuthenticationException e) {
        Failure failure = new Failure();
        failure.setError(RestResponse.ERROR_UNAUTHORIZED_REQUEST);
        failure.setDescription(e.getMessage());
        return ResponseEntity.ok(failure);
    }
}
