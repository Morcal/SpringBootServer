package cn.com.xinli.portal.configuration;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class Api {
    private final String scope;
    private final String action;
    private final String url;
    private final String method;
    private final String response;

    /**
     * Construct an Api.
     *
     * @param scope    api scope.
     * @param action   api action.
     * @param url      api HTTP URL.
     * @param method   api HTTP method.
     * @param response api HTTP response format.
     */
    public Api(@NotNull String scope,
               @NotNull String action,
               @NotNull String url,
               @NotNull String method,
               @NotNull String response) {
        this.scope = scope;
        this.action = action;
        this.url = url;
        this.method = method;
        this.response = response;
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

    private String getText() {
        return StringUtils.join(
                new Object[] { scope, action, url, method, response}, ", ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Api)) return false;

        Api api = (Api) o;
        return scope.equals(api.getScope())
                && action.equals(api.getAction())
                && url.equals(api.getUrl())
                && method.equals(api.getMethod())
                && response.equals(api.getResponse());
    }

    @Override
    public int hashCode() {
        return getText().hashCode();
    }

    @Override
    public String toString() {
        return "API: " + getText();
    }
}
