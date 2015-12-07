package cn.com.xinli.portal.configuration;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Api registration.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class ApiRegistration {
    /** Api type. */
    private final String type;

    /** Api version. */
    private final String version;

    /** Authorize URL. */
    private final String authorizeURL;

    /** APIs this registration provides. */
    private final List<Api> apis;

    /** base URL. */
    private final String baseUrl;

    public ApiRegistration(String type, String version, String authorizeURL) {
        this.type = type;
        this.version = version;
        this.baseUrl = "/" + version;
        this.authorizeURL = baseUrl + authorizeURL;
        this.apis = new ArrayList<>();
    }

    /**
     * Get Api type.
     * @return Api type.
     */
    public String getType() {
        return type;
    }

    /**
     * Get Api version.
     * @return Api version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get Api Authorize URL.
     *
     * If Api does NOT provide an authorize URL, it will return null.
     * @return Api Authorize URL.
     */
    public String getAuthorizeURL() {
        return authorizeURL;
    }

    /**
     * Get all APIs.
     * @return Api list.
     */
    public List<Api> getApis() {
        return apis;
    }

    /**
     * Register a new Api.
     * @param api Api.
     * @throws ConfigurationException
     */
    public synchronized Api registerApi(@NotNull Api api) throws ConfigurationException {
        if (apis.contains(api)) {
            throw new ConfigurationException("api: "
                    + api.toString() + " already registered");
        }
        apis.add(api);
        return api;
    }
}
