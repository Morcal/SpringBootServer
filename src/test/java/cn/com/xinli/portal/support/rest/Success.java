package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of successful operations.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Success extends RestResponse {
    @JsonProperty("authorization")
    private Authorization authorization;

    @JsonProperty("authentication")
    private Authentication authentication;

    @JsonProperty("session")
    @JsonIgnore
    private Object session;

    public Authorization getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
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
                ", authentication=" + authentication +
                '}';
    }
}
