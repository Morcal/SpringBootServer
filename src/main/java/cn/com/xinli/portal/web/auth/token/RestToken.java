package cn.com.xinli.portal.web.auth.token;

import org.springframework.security.core.token.Token;

/**
 * REST token.
 *
 * <p>REST tokens covers {@link TokenScope#PORTAL_ACCESS_TOKEN_SCOPE},
 * {@link TokenScope#PORTAL_SESSION_TOKEN_SCOPE} and {@link TokenScope#SYSTEM_ADMIN_TOKEN_SCOPE}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/12.
 */
public final class RestToken implements Token {
    /** Rest Token key. */
    private final String key;

    private final String type;

    private final TokenKey tokenKey;

    public RestToken(String key, String type, TokenKey tokenKey) {
        this.key = key;
        this.type = type;
        this.tokenKey = tokenKey;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final long getKeyCreationTime() {
        return tokenKey.getCreationTime();
    }

    @Override
    public String getExtendedInformation() {
        return tokenKey.getExtendedInformation();
    }

    public TokenScope getScope() {
        return tokenKey.getScope();
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RestToken{" +
                "key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", tokenKey=" + tokenKey +
                '}';
    }
}
