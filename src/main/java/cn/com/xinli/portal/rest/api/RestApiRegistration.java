package cn.com.xinli.portal.rest.api;

import cn.com.xinli.portal.configuration.ConfigurationException;
import cn.com.xinli.portal.rest.api.RestApi;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * RestApi registration.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class RestApiRegistration {
    /** RestApi type. */
    private final String type;

    /** RestApi version. */
    private final String version;

    /** Authorize URL. */
    private final String authorizeURL;

    /** APIs this registration provides. */
    private final List<RestApi> apis;

    /** base URL. */
    private final String baseUrl;

    public RestApiRegistration(String type, String version, String authorizeURL) {
        this.type = type;
        this.version = version;
        this.baseUrl = "/" + version;
        this.authorizeURL = baseUrl + authorizeURL;
        this.apis = new ArrayList<>();
    }

    /**
     * Get RestApi type.
     * @return RestApi type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get RestApi version.
     * @return RestApi version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get RestApi Authorize URL.
     *
     * If RestApi does NOT provide an authorize URL, it will return null.
     * @return RestApi Authorize URL.
     */
    public String getAuthorizeURL() {
        return authorizeURL;
    }

    /**
     * Get all APIs.
     * @return RestApi list.
     */
    public List<RestApi> getApis() {
        return apis;
    }

    /**
     * Register a new RestApi.
     * @param api RestApi.
     * @throws ConfigurationException
     */
    public synchronized RestApi registerApi(@NotNull RestApi api) throws ConfigurationException {
        if (apis.contains(api)) {
            throw new ConfigurationException("api: "
                    + api.toString() + " already registered");
        }
        apis.add(api);
        return api;
    }
}
