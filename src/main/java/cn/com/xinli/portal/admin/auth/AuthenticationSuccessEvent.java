package cn.com.xinli.portal.admin.auth;

import cn.com.xinli.portal.admin.Activity;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class AuthenticationSuccessEvent extends AbstractRestAuthenticationEvent {
    public AuthenticationSuccessEvent(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) {
        super(request, response, authentication, Activity.Severity.VERBOSE);
    }
}
