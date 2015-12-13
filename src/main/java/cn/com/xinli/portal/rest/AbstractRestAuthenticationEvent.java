package cn.com.xinli.portal.rest;

import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public abstract class AbstractRestAuthenticationEvent extends AbstractAuthenticationEvent {
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;

    public AbstractRestAuthenticationEvent(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Authentication authentication) {
        super(authentication);
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
