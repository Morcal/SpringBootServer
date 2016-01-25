package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.web.auth.HttpDigestCredentials;

import java.util.Map;

/**
 * REST Request.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/10.
 */
public interface RestRequest {
    /**
     * Get rest request method, normally HTTP method.
     * @return rest request method.
     */
    String getMethod();

    /**
     * Get request uri.
     * @return uri.
     */
    String getUri();

    /**
     * Get rest request credentials.
     * @return rest request credentials.
     */
    HttpDigestCredentials getCredentials();

    /**
     * Get request parameters.
     * @return request parameters within a {@link java.util.Collections.UnmodifiableMap}.
     */
    Map<String, String[]> getParameters();

    /**
     * get rest request parameter.
     * @param name parameter name.
     * @return parameter value.
     */
    String getParameter(String name);

    /**
     * Set authentication/authorization parameter.
     * @param key parameter key.
     * @param value parameter value.
     */
    RestRequest setAuthParameter(String key, String value);

    /**
     * Set request parameter.
     * @param key parameter key.
     * @param value parameter value.
     */
    RestRequest setParameter(String key, String value);

    /**
     * Sign rest request with key..
     * @param key signing key.
     */
    void sign(String key);
}
