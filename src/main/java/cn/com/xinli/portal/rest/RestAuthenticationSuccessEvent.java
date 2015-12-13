package cn.com.xinli.portal.rest;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class RestAuthenticationSuccessEvent extends AbstractRestAuthenticationEvent {
    public RestAuthenticationSuccessEvent(HttpServletRequest request,
                                          HttpServletResponse response,
                                          Authentication authentication) {
        super(request, response, authentication);
    }
}
