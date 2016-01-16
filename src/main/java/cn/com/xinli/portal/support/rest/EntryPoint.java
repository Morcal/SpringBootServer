package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

/**
 * PWS REST API Entry Point.
 * 
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/6.
 */
public class EntryPoint {
    private String scope;
    private String action;
    private String url;
    private String method;
    private String response;

    @JsonProperty("requires_auth")
    private boolean requiresAuth;

    /** Default constructor for JSON parser. */
    public EntryPoint() {
        this.scope = "";
        this.action = "";
        this.url = "";
        this.method = "";
        this.response = "";
        this.requiresAuth = false;
    }

    /**
     * Construct an EntryPoint.
     *
     * @param scope    scope.
     * @param action   action.
     * @param url      HTTP URL.
     * @param method   HTTP method.
     * @param response HTTP response format.
     */
    public EntryPoint(String scope,
                      String action,
                      String url,
                      String method,
                      String response,
                      boolean requiresAuth) {
        this.scope = scope;
        this.action = action;
        this.url = url;
        this.method = method;
        this.response = response;
        this.requiresAuth = requiresAuth;
    }

    public String getScope() {
        return scope;
    }

    public String getAction() {
        return action;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public String getResponse() {
        return response;
    }

    public boolean requiresAuth() {
        return requiresAuth;
    }

    private String getText() {
        StringJoiner joiner = new StringJoiner(", ");
        joiner.add("scope=" + scope)
                .add("action="+ action)
                .add("url=" + url)
                .add("method=" + method)
                .add("response="+ response)
                .add("requiresAuth=" + requiresAuth);
        return joiner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryPoint)) return false;

        EntryPoint entry = (EntryPoint) o;
        return scope.equals(entry.getScope())
                && action.equals(entry.getAction())
                && url.equals(entry.getUrl())
                && method.equals(entry.getMethod())
                && response.equals(entry.getResponse())
                && requiresAuth == entry.requiresAuth();
    }

    @Override
    public int hashCode() {
        return getText().hashCode();
    }

    @Override
    public String toString() {
        return "REST Entry Point: {" + getText() + "}";
    }
}
