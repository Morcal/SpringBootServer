package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.Constants;
import cn.com.xinli.portal.auth.HttpDigestAuthentication;
import cn.com.xinli.portal.util.SignatureUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * PWS REST APIs request.
 *
 * Project: portal-rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class RestRequest {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestRequest.class);

    private final HttpDigestAuthentication authentication;
    private final Map<String, String> parameters;
    private final String url;
    private final String method;

    /**
     * Sole constructor.
     * @param authentication http digest authentication.
     * @param parameters http request parameters.
     */
    private RestRequest(String url,
                        String method,
                        HttpDigestAuthentication authentication,
                        Map<String, String> parameters) {
        this.url = url;
        this.method = method;
        this.authentication = authentication;
        this.parameters = parameters;
    }

    public String getCredentials() {
        return HttpDigestAuthentication.getCredentials(authentication);
    }

    @Override
    public String toString() {
        return "RestRequest{" +
                "Authentication: " + getCredentials() +
                "}, parameters=" + parameters +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    /**
     * Sign request with private key.
     * @param privateKey private key.
     */
    public void sign(final String privateKey) {
        String signature = SignatureUtil.sign(
                (method.toUpperCase() + "&" + collect()).getBytes(),
                privateKey,
                authentication.getParameter(HttpDigestAuthentication.SIGNATURE_METHOD));
        authentication.setParameter(HttpDigestAuthentication.SIGNATURE, signature);
    }

    /**
     * Collect REST HTTP request parameters.
     *
     * sort parameters alphabetically by encoded key.
     * join them with format key="value" and '=' as delimiter.
     * @return collected string.
     */
    private String collect() {
        StringJoiner joiner = new StringJoiner("&");
        //parameters.entrySet().stream().sorted().forEach();
        parameters.entrySet().stream().forEach(entry -> {
            try {
                joiner.add(URLEncoder.encode(entry.getKey(), Constants.DEFAULT_CHAR_ENCODING) +
                        "=" + URLEncoder.encode(entry.getValue(), Constants.DEFAULT_CHAR_ENCODING));
            } catch (UnsupportedEncodingException e) {
                /* ignore unsupported encoded parameters. */
                log.error("Failed to encode request, parameter: {" +
                        entry.getKey() + "=" + entry.getValue() + "}.");
            }
        });

        return joiner.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpDigestAuthentication authentication;
        private Map<String, String> parameters;
        private String url;
        private String method;

        public Builder() {
            authentication = new HttpDigestAuthentication();
            parameters = new HashMap<>();
        }

        public Builder setAuthParam(String name, String value) {
            authentication.setParameter(name, value);
            return this;
        }

        public Builder setParameter(String name, String value) {
            parameters.put(name, value);
            return this;
        }

        public Builder setUrl(String url) {
            if (StringUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url can not be empty.");
            }
            this.url = url;
            return this;
        }

        public Builder setMethod(String method) {
            if (StringUtils.isEmpty(url)) {
                throw new IllegalArgumentException("url can not be empty.");
            }
            this.method = method;
            return this;
        }

        public RestRequest build() {
            if (StringUtils.isEmpty(url)) {
                throw new IllegalStateException("url not been set.");
            }

            if (StringUtils.isEmpty(method)) {
                throw new IllegalStateException("method not been set.");
            }

            return new RestRequest(url, method, authentication, parameters);
        }
    }
}
