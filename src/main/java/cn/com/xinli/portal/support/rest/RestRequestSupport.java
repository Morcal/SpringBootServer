package cn.com.xinli.portal.support.rest;

import cn.com.xinli.portal.util.CodecUtil;
import cn.com.xinli.portal.auth.HttpDigestCredentials;
import cn.com.xinli.portal.util.SignatureUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST APIs request.
 * <p>
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class RestRequestSupport implements RestRequest {
    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(RestRequestSupport.class);

    /**
     * Credentials.
     */
    private final HttpDigestCredentials credentials;

    /**
     * Request parameters.
     */
    private final Map<String, String[]> parameters;

    /**
     * Request method.
     */
    private final String method;

    /**
     * Request uri.
     */
    private final String uri;

    public RestRequestSupport(String method, String uri) {
        this.method = method;
        this.uri = uri;
        credentials = new HttpDigestCredentials();
        parameters = Collections.synchronizedMap(new HashMap<>());
    }


    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUri() {
        return uri;
    }

    /**
     * Sign request with private key.
     *
     * @param privateKey private key.
     */
    @Override
    public void sign(String privateKey) {
        credentials.removeParameter(HttpDigestCredentials.SIGNATURE);

        String joined;
        try {
            StringJoiner joiner = new StringJoiner("&");
            joiner.add(method.toUpperCase())
                    .add(CodecUtil.urlEncode(uri))
                    .add(credentials.getCredentialsWithoutSignature())
                    .add(collect());
            joined = joiner.toString();
        } catch (UnsupportedEncodingException e) {
            /* fall back to original url. */
            StringJoiner joiner = new StringJoiner("&");
            joiner.add(method.toUpperCase())
                    .add(uri)
                    .add(credentials.getCredentialsWithoutSignature())
                    .add(collect());
            joined = joiner.toString();
        }

        if (logger.isTraceEnabled()) {
            logger.trace("> sign on: {{}}", joined);
        }

        String signature = SignatureUtil.sign(
                joined.getBytes(),
                privateKey,
                credentials.getParameter(HttpDigestCredentials.SIGNATURE_METHOD));
        credentials.setParameter(HttpDigestCredentials.SIGNATURE, signature);

        if (logger.isTraceEnabled()) {
            logger.trace("> signature: {{}}", signature);
        }
    }

    @Override
    public HttpDigestCredentials getCredentials() {
        return credentials;
    }

    @Override
    public String getParameter(String name) {
        String[] values = parameters.get(name);
        return values == null ? null : values[0];
    }

    @Override
    public Map<String, String[]> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public RestRequest setAuthParameter(String key, String value) {
        credentials.setParameter(key, value);
        return this;
    }

    @Override
    public RestRequest setParameter(String key, String value) {
        synchronized (parameters) {
            if (parameters.containsKey(key)) {
                List<String> values = new ArrayList<>();
                values.addAll(Arrays.asList(parameters.get(key)));
                values.add(StringUtils.defaultString(value, ""));
                String[] newValues = new String[values.size()];
                newValues = values.toArray(newValues);
                parameters.put(key, newValues);
            } else {
                String values[] = new String[1];
                values[0] = StringUtils.defaultString(value, "");
                parameters.put(key, values);
            }
        }
        return this;
    }

    private int compareParameter(Map.Entry<String, String> one, Map.Entry<String, String> other) {
        int key = one.getKey().compareTo(other.getKey());
        return key == 0 ? one.getValue().compareTo(other.getValue()) : key;
    }

    /**
     * Collect REST HTTP request parameters.
     * <p>
     * sort parameters alphabetically by encoded key.
     * join them with format key="value" and '=' as delimiter.
     *
     * @return collected string.
     */
    private String collect() {
        final Map<String, String> encoded = new HashMap<>();

        synchronized (parameters) {
            parameters.forEach((key, value) -> {
                try {
                    for (String v : value) {
                        encoded.put(CodecUtil.urlEncode(key), CodecUtil.urlEncode(v));
                    }
                } catch (UnsupportedEncodingException e) {
                        /* ignore unsupported encoded parameters. */
                    logger.error("Failed to encode request, parameters: {}", parameters);
                }
            });
        }

        return encoded.entrySet().stream().sorted(this::compareParameter)
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
    }

    /**
     * Convert {@link #parameters} to a string.
     * @return string.
     */
    private String parametersToString() {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + StringUtils.join(entry.getValue(), ", "))
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "RestRequestSupport{" +
                "credentials=" + credentials +
                ", parameters=" + parametersToString() +
                '}';
    }
}
