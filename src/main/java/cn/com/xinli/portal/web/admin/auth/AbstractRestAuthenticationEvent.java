package cn.com.xinli.portal.web.admin.auth;

import cn.com.xinli.portal.core.Activity;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
public abstract class AbstractRestAuthenticationEvent extends AbstractAuthenticationEvent {
    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected final Activity.Severity severity;

    public AbstractRestAuthenticationEvent(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Authentication authentication,
                                           Activity.Severity severity) {
        super(authentication);
        this.request = request;
        this.response = response;
        this.severity = severity;
    }

    public final Activity.Severity getSeverity() {
        return this.severity;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
