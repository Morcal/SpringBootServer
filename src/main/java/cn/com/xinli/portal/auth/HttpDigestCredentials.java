package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.util.CodecUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * HTTP Authentication credential.
 *
 * <p>PWS REST APIs authentication is built on HTTP headers.
 * Which means that incoming REST API requests must provide authentiaction
 * credentials inside http headers.
 *
 * <p>The authentication credentials requires certain encoding and
 * additional signature which should be calculated against credentials itself,
 * HTTP request entities and secret key shared by PWS and client vendors.
 *
 * <p>Project: rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class HttpDigestCredentials {
    /** Log. */
    private static final Logger logger = LoggerFactory.getLogger(HttpDigestCredentials.class);

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
    public static final String CREDENTIAL_DELIMITER_REGEX = ", ";

    private static final String CREDENTIAL_REGEXP = "^([a-zA-Z0-9_]+)=\"(.*)\"$";

    /** Parameters, credentials parameters should be kept in order. */
    private Map<String, String> parameters= new LinkedHashMap<>();

    /** Parameters allowed within credentials. */
    private static final String[] allowed = {
            NONCE, RESPONSE, CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /** Parameters required by a challenge response. */
    private static final String[] CHALLENGE_RESPONSE_REQUIRES = {
            NONCE, RESPONSE, CLIENT_ID, SIGNATURE,
            SIGNATURE_METHOD, TIMESTAMP, VERSION
    };

    /** Parameters required by a client token. */
    private static final String[] ACCESS_TOKEN_REQUIRES = {
            CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, VERSION
    };

    /** Parameters required by a session token. */
    private static final String[] SESSION_TOKEN_REQUIRES = {
            CLIENT_ID, SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /**
     * Get credential parameter.
     * @param name parameter name.
     * @return parameter value.
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }

    /**
     * Get credentials parameters.
     * @return {@link java.util.Collections.UnmodifiableMap} contains parameters.
     */
    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Remove parameter by name.
     * @param name parameter name.
     * @return removed parameter value.
     */
    public String removeParameter(String name) {
        return parameters.remove(name);
    }

    /**
     * Copy credentials from other one.
     * @param source other credentials copy from.
     */
    public void copy(HttpDigestCredentials source) {
        source.parameters.forEach((s, o) -> parameters.put(s, o));
    }

    /**
     * Create a http digest authentication from HTTP HEADER Authentication.
     * @param httpAuthentication HTTP Header Authentication credentials.
     * @return http digest authentication.
     * @throws BadRestCredentialsException if authentication not well-formed.
     */
    public static HttpDigestCredentials of(String httpAuthentication) throws BadRestCredentialsException {
        if (StringUtils.isEmpty(httpAuthentication)) {
            throw new IllegalArgumentException("HTTP Digest authentication credentials can not be empty.");
        }

        if (!httpAuthentication.startsWith(SCHEME + " ")) {
            throw new BadRestCredentialsException("Invalid authentication scheme.");
        }

        HttpDigestCredentials credentials = new HttpDigestCredentials();

        try {
            String content = httpAuthentication.substring(httpAuthentication.indexOf(SCHEME) + SCHEME.length() + 1);
            /* In case of credentials ends with CREDENTIAL_DELIMITER_REGEX and
             * it will work even if it's wrong, so we trim the content
             * So that kind of credentials will be recognized as invalid.
             */
            content = content.trim();
            for (String pair : content.split(CREDENTIAL_DELIMITER_REGEX)) {
                Matcher matcher = Pattern.compile(CREDENTIAL_REGEXP).matcher(pair.trim());
                if (!matcher.matches() || matcher.groupCount() < 2) {
                    throw new BadRestCredentialsException("Invalid authentication credential.");
                }

                String key = matcher.group(1), value = matcher.group(2);
                value = CodecUtil.urlDecode(value);
                credentials.setParameter(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            throw new BadRestCredentialsException("Unsupported encoding in credentials.", e);
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
        if (StringUtils.isEmpty(name) || value == null ||
                !ArrayUtils.contains(allowed, name)) {
            throw new IllegalArgumentException("Illegal HTTP Digest authentication parameter, " +
                    name + "=" + value);
        }
        parameters.put(name, value);
    }

    /**
     * Get credentials without signature.
     * @return credentials in string form without signature.
     */
    public String getCredentialsWithoutSignature() {
        return SCHEME + " " + parameters.entrySet().stream()
                .filter(e -> !e.getKey().equals(SIGNATURE))
                .map(entry -> {
                    try {
                        return CodecUtil.urlEncode(entry.getKey()) + "=\"" +
                                CodecUtil.urlEncode(entry.getValue()) + "\"";
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Failed to encode credential: {}", toString());
                        return "";
                    }
                })
                .collect(Collectors.joining(CREDENTIAL_DELIMITER_REGEX));
    }

    /**
     * Get HTTP digest authentication credentials.
     * @return HTTP digest authentication credentials.
     */
    public String getCredentials() {
        return SCHEME + " " + parameters.entrySet().stream()
                .map(entry -> {
                    try {
                        return CodecUtil.urlEncode(entry.getKey()) + "=\"" +
                                CodecUtil.urlEncode(entry.getValue()) + "\"";
                    } catch (UnsupportedEncodingException e) {
                        logger.error("Failed to encode credential: {}", toString());
                        return "";
                    }
                })
                .collect(Collectors.joining(CREDENTIAL_DELIMITER_REGEX));
    }

    /**
     * Check if digest authentication credentials contains challenge.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains challenge.
     */
    public static boolean containsChallenge(final HttpDigestCredentials credentials) {
        Optional<String> opt = Stream.of(CHALLENGE_RESPONSE_REQUIRES)
                .filter(name -> !credentials.parameters.containsKey(name))
                .findAny();

        return !opt.isPresent() &&
                credentials.parameters.keySet().stream().anyMatch(key -> key.equals(NONCE)) &&
                credentials.parameters.keySet().stream().anyMatch(key -> key.equals(RESPONSE));
    }

    /**
     * Check if digest authentication credentials contains client token.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains a client token.
     */
    public static boolean containsAccessToken(final HttpDigestCredentials credentials) {
        /* Integrity check. */
        Map<String, String> parameters = credentials.parameters;

        Optional<String> opt = Stream.of(ACCESS_TOKEN_REQUIRES)
                .filter(name -> !parameters.containsKey(name))
                .findAny();

        return !opt.isPresent() &&
                parameters.containsKey(CLIENT_TOKEN);
    }

    /**
     * Check if digest authentication credentials contains session token.
     * @param credentials HTTP digest authentication credentials.
     * @return true if credentials contains a session token.
     */
    public static boolean containsSessionToken(final HttpDigestCredentials credentials) {
        /* Integrity check. */
        Map<String, String> parameters = credentials.parameters;

        Optional<String> opt = Stream.of(SESSION_TOKEN_REQUIRES)
                .filter(name -> !parameters.containsKey(name))
                .findAny();

        return !opt.isPresent() &&
                parameters.containsKey(SESSION_TOKEN);
    }

    @Override
    public String toString() {
        return "HttpDigestCredentials{" +
                "parameters=" + parameters +
                '}';
    }
}
