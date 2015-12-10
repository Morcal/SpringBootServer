package cn.com.xinli.portal.rest.api.v1;

import cn.com.xinli.portal.Constants;
import cn.com.xinli.portal.rest.RestRequest;
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
public class RestRequestImpl implements RestRequest {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestRequestImpl.class);

    private final HttpDigestCredentials authentication;
    private final Map<String, String> parameters;
    private final String url;
    private final String method;

    /**
     * Sole constructor.
     * @param authentication http digest authentication.
     * @param parameters http request parameters.
     */
    private RestRequestImpl(String url,
                            String method,
                            HttpDigestCredentials authentication,
                            Map<String, String> parameters) {
        this.url = url;
        this.method = method;
        this.authentication = authentication;
        this.parameters = parameters;
    }

    public String getCredentials() {
        return authentication.getCredentials();
    }

    @Override
    public String toString() {
        return "RestRequestImpl: {" +
                "{Authentication: " + getCredentials() +
                "}, parameters=" + parameters +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                '}';
    }

    /**
     * Sign request with private key.
     * @param key private key.
     */
    @Override
    public void sign(final String key) {
        /* Remove signature before sign. */
        authentication.removeParameter(HttpDigestCredentials.SIGNATURE);
        StringJoiner joiner = new StringJoiner("&");
        joiner.add(method.toUpperCase())
                .add(url)
                .add(authentication.getCredentials())
                .add(collect());
        log.debug("Request to sign: " + joiner.toString());
        String signature = SignatureUtil.sign(
                joiner.toString().getBytes(),
                key,
                authentication.getParameter(HttpDigestCredentials.SIGNATURE_METHOD));
        /* Reassign signature back to credentials. */
        authentication.setParameter(HttpDigestCredentials.SIGNATURE, signature);
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
        parameters.entrySet().stream().sorted().forEach(entry -> {
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
        private HttpDigestCredentials authentication;
        private Map<String, String> parameters;
        private String url;
        private String method;

        public Builder() {
            authentication = new HttpDigestCredentials();
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

        public RestRequestImpl build() {
            if (StringUtils.isEmpty(url)) {
                throw new IllegalStateException("url not been set.");
            }

            if (StringUtils.isEmpty(method)) {
                throw new IllegalStateException("method not been set.");
            }

            return new RestRequestImpl(url, method, authentication, parameters);
        }
    }
}
