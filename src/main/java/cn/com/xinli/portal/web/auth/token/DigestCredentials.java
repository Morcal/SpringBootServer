package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.support.RestCreadentials;
import cn.com.xinli.portal.util.Asserts;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Digest Credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/3/14.
 */
public class DigestCredentials implements RestCreadentials {

    /** Challenge nonce attribute name. */
    public static final String NONCE = "nonce";

    /** Challenge response attribute name. */
    public static final String RESPONSE = "response";

    /** Client token attribute name. */
    public static final String CLIENT_TOKEN = "client_token";

    /** Client id attribute name. */
    public static final String CLIENT_ID = "client_id";

    /** Request signature attribute name. */
    public static final String SIGNATURE = "signature";

    /** Request signature method attribute name. */
    public static final String SIGNATURE_METHOD = "signature_method";

    /** Timestamp attribute name. */
    public static final String TIMESTAMP = "timestamp";

    /** Session token attribute name. */
    public static final String SESSION_TOKEN = "session_token";

    /** Version attribute name. */
    public static final String VERSION = "version";

    /** Default version value. */
    public static final String VERSION_VALUE = "1.0";

    /** Default digest algorithm name. */
    public static final String DIGEST_ALGORITHM = "HMAC-SHA1";

    public static final String ADMIN_TOKEN = "admin_token";

    /** All attribute names within credentials. */
    public static final String[] ATTRIBUTE_NAMES = {
            NONCE, RESPONSE, CLIENT_TOKEN, CLIENT_ID, ADMIN_TOKEN,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /** Attributes required by a challenge response. */
    public static final String[] CHALLENGE_RESPONSE_REQUIRES = {
            NONCE, RESPONSE, CLIENT_ID, SIGNATURE,
            SIGNATURE_METHOD, TIMESTAMP, VERSION
    };

    /** Attributes required by a client token. */
    public static final String[] ACCESS_TOKEN_REQUIRES = {
            CLIENT_TOKEN, CLIENT_ID,
            SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, VERSION
    };

    /** Attributes required by a session token. */
    public static final String[] SESSION_TOKEN_REQUIRES = {
            CLIENT_ID, SIGNATURE, SIGNATURE_METHOD, TIMESTAMP, SESSION_TOKEN, VERSION
    };

    /** Attributes, credentials attributes should be kept in order. */
    private Map<String, String> attributes= new LinkedHashMap<>();

    /**
     * Clear attributes.
     */
    public void clear() {
        attributes.clear();
    }

    @Override
    public String getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public String removeAttribute(String name) {
        return attributes.remove(name);
    }

    @Override
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    protected Set<Map.Entry<String, String>> getAttributes() {
        return Collections.unmodifiableSet(attributes.entrySet());
    }

    @Override
    public void setAttribute(String name, String value) {
        Asserts.notBlank(name, "Digest credentials");

        if (!ArrayUtils.contains(ATTRIBUTE_NAMES, name)) {
            throw new IllegalArgumentException("Illegal HTTP Digest authentication parameter, " +
                    name + "=" + value);
        }

        if (value == null) {
            removeAttribute(name);
        } else {
            attributes.put(name, value);
        }
    }

    /**
     * Clear this credentials and Copy credentials from other one.
     *
     * <p>If source credentials is null, this credentials will be cleared.
     * @param source other credentials copy from.
     */
    public void copy(DigestCredentials source) {
        clear();
        if (source != null) {
            for (String name : ATTRIBUTE_NAMES) {
                String value = source.getAttribute(name);
                setAttribute(name, value);
            }
        }
    }

    @Override
    public boolean containsNonce() {
        return !StringUtils.isEmpty(attributes.get(NONCE));
    }

    @Override
    public boolean containsChallengeResponse() {
        for (String name : CHALLENGE_RESPONSE_REQUIRES) {
            if (!attributes.containsKey(name))
                return false;
        }

        return attributes.containsKey(NONCE) && attributes.containsKey(RESPONSE);
    }

    @Override
    public boolean containsAccessToken() {
        for (String name : ACCESS_TOKEN_REQUIRES) {
            if (!attributes.containsKey(name))
                return false;
        }

        return attributes.containsKey(CLIENT_TOKEN);
    }

    @Override
    public boolean containsSessionToken() {
        for (String name : SESSION_TOKEN_REQUIRES) {
            if (!attributes.containsKey(name))
                return false;
        }

        return attributes.containsKey(SESSION_TOKEN);
    }

    @Override
    public boolean containsAdminToken() {
        return attributes.containsKey(ADMIN_TOKEN);
    }

    /**
     * Output attributes to plain text string.
     * @return string.
     */
    private String attributesAsString() {
        return getAttributes().stream()
                .map(entry ->  entry.getValue() + "='" + entry.getValue() + "'")
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return "DigestCredentials{" +
                "attributes=" + attributesAsString() +
                '}';
    }
}
