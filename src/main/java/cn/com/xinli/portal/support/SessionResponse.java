package cn.com.xinli.portal.support;

import cn.com.xinli.rest.bean.Authentication;
import cn.com.xinli.rest.bean.Authorization;
import cn.com.xinli.rest.bean.RestBean;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Portal Session REST response bean.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public class SessionResponse implements RestBean {
    @JsonProperty("authorization")
    private Authorization authorization;

    @JsonProperty("session")
    private SessionBean session;

    @JsonProperty("authentication")
    private Authentication authentication;

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    public SessionBean getSession() {
        return session;
    }

    public void setSession(SessionBean session) {
        this.session = session;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        return "Success{" +
                "authorization=" + authorization +
                ", session=" + session +
                ", authentication=" + authentication +
                '}';
    }
}
