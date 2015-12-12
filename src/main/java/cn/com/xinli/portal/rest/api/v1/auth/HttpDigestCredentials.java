package cn.com.xinli.portal.rest.api.v1.auth;

import cn.com.xinli.portal.Constants;
import cn.com.xinli.portal.rest.Credentials;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP Authentication credential.
 *
 * Project: portal-rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class HttpDigestCredentials implements Credentials {
    /** Log. */
    private static final Log log = LogFactory.getLog(HttpDigestCredentials.class);

    public static final String HEADER_NAME = "X-Xinli-Auth";
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

    private static final String CREDENTIAL_REGEXP = "^([a-zA-Z0-9_]+)=(\".*\")$";

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

    private static final String[] SESSION_TOKEN_REQUIRES = {
            SCHEME, CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /** Parameters. */
    private Map<String, String> parameters= new HashMap<>();

    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Create a http digest authentication from HTTP HEADER Authentication.
     * @param httpAuthentication HTTP Header Authentication credentials.
     * @return http digest authentication.
     * @throws BadCredentialsException if authentication not well-formed.
     */
    public static HttpDigestCredentials of(String httpAuthentication) {
        if (StringUtils.isEmpty(httpAuthentication)) {
            throw new IllegalArgumentException("HTTP Digest authentication credentials can not be empty.");
        }

        if (!httpAuthentication.startsWith(SCHEME + " ")) {
            throw new BadCredentialsException("Invalid authentication scheme.");
        }

        HttpDigestCredentials credentials = new HttpDigestCredentials();

        try {
            String content = httpAuthentication.substring(httpAuthentication.indexOf(SCHEME) + SCHEME.length() + 1);
            StringTokenizer tokenizer = new StringTokenizer(content, ",");
            while (tokenizer.hasMoreTokens()) {
                Matcher matcher = Pattern.compile(CREDENTIAL_REGEXP).matcher(tokenizer.nextToken().trim());
                if (!matcher.matches() || matcher.groupCount() < 3) {
                    throw new BadCredentialsException("Invalid authentication credential.");
                }

                String key = matcher.group(1), value = matcher.group(2);
                value = URLDecoder.decode(value, Constants.DEFAULT_CHAR_ENCODING);
                credentials.setParameter(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BadCredentialsException("Unsupported encoding in credentials.", e);
        }

        return credentials;
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
     * Remove parameter with name.
     * @param name parameter name.
     */
    public void removeParameter(String name) {
        parameters.remove(name);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Get HTTP digest authentication credentials.</p>
     * @return HTTP digest authentication credentials.
     */
    @Override
    public String getCredentials() {
        StringJoiner joiner = new StringJoiner(", ");
        parameters.entrySet().forEach(entry -> {
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
     * Check if digest authentication credentials contains challenge.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains challenge.
     */
    public static boolean containsChallenge(final HttpDigestCredentials credentials) {
        return credentials.parameters.keySet().stream()
                .anyMatch(key -> !ArrayUtils.contains(CHALLENGE_RESPONSE_REQUIRES, key));
    }

    /**
     * Check if digest authentication credentials contains client token.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains a client token.
     */
    public static boolean containsToken(final HttpDigestCredentials credentials) {
        return credentials.parameters.keySet().stream()
                .anyMatch(key -> !ArrayUtils.contains(CLIENT_TOKEN_REQUIRES, key));
    }

    /**
     * Check if digest authentication credentials contains session token.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains a session token.
     */
    public static boolean containsSessionToken(final HttpDigestCredentials credentials) {
        return credentials.parameters.keySet().stream()
                .anyMatch(key -> !ArrayUtils.contains(SESSION_TOKEN_REQUIRES, key));
    }
}
