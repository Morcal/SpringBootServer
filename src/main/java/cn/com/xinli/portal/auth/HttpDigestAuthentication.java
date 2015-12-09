package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * HTTP Authentication credential.
 *
 * Project: portal-rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class HttpDigestAuthentication {
    /** Log. */
    private static final Log log = LogFactory.getLog(HttpDigestAuthentication.class);

    public static final String SCHEME = "Digest";
    public static final String NONCE = "nonce";
    public static final String RESPONSE = "response";
    public static final String CLIENT_TOKEN = "client_token";
    public static final String CLIENT_ID = "client_id";
    public static final String SIGNATURE = "signature";
    public static final String SIGNATURE_METHOD = "signature_method";
    public static final String TIMESTAMP = "timestamp";
    public static final String SESSION_TOKEN = "session_token";
    public static final String VERSION = "version";

    private static final String[] allowed = {
            SCHEME, NONCE, RESPONSE, CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    private static final String[] CHALLENGE_RESPONSE_REQUIRES = {
            SCHEME, NONCE, RESPONSE, CLIENT_ID, SIGNATURE,
            SIGNATURE_METHOD, TIMESTAMP, VERSION
    };

    private static final String[] CLIENT_TOKEN_REQUIRES = {
            SCHEME, CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /** Parameters. */
    private Map<String, String> parameters= new HashMap<>();

    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Set HTTP digest authentication parameter.
     * @param name parameter name.
     * @param value parameter value.
     * @throws IllegalArgumentException if name or value is null
     * or name is not allowed.
     */
    public void setParameter(String name, String value) {
        if (StringUtils.isEmpty(name) || StringUtils.isEmpty(value) ||
                !ArrayUtils.contains(allowed, name)) {
            throw new IllegalArgumentException("Illegal HTTP Digest authentication parameter, " +
                    name + "=" + value);
        }
        parameters.put(name, value);
    }

    /**
     * Get HTTP digest authentication credentials.
     * @param auth HTTP digest authentication.
     * @return HTTP digest authentication credentials.
     */
    public static String getCredentials(final HttpDigestAuthentication auth) {
        StringJoiner joiner = new StringJoiner(", ");
        auth.parameters.entrySet().forEach(entry -> {
            try {
                joiner.add(URLEncoder.encode(entry.getKey(), Constants.DEFAULT_CHAR_ENCODING) +
                        "=\"" + URLEncoder.encode(entry.getValue(), Constants.DEFAULT_CHAR_ENCODING) +
                        "\"");
            } catch (UnsupportedEncodingException e) {
                log.error("Failed to encode digest, {" +
                        entry.getKey() + "=" + entry.getValue() + "}.");
            }
        });

        return SCHEME + " " + joiner.toString();
    }

    /**
     * Check if digest authentication is valid challenge response.
     * @param auth HTTP digest authentication.
     * @return true if valid.
     */
    public static boolean isValidChallenge(final HttpDigestAuthentication auth) {
        return auth.parameters.keySet().stream()
                .anyMatch(key -> !ArrayUtils.contains(CHALLENGE_RESPONSE_REQUIRES, key));
    }

    /**
     * Check if digest authentication is valid token based.
     * @param auth HTTP digest authentication.
     * @return true if valid.
     */
    public static boolean isValidToken(final HttpDigestAuthentication auth) {
        return auth.parameters.keySet().stream()
                .anyMatch(key -> !ArrayUtils.contains(CLIENT_TOKEN_REQUIRES, key));
    }
}
