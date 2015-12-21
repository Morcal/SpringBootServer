package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.Activity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class AuthenticationFailureEvent extends AbstractRestAuthenticationEvent {
    private final AuthenticationException exception;
    public AuthenticationFailureEvent(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication,
                                      AuthenticationException e) {
        super(request, response, authentication, Activity.Severity.VERBOSE);
        exception = e;
    }

    public AuthenticationException getException() {
        return exception;
    }
}
