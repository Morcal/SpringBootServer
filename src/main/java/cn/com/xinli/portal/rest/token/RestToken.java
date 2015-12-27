package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.core.token.Token;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;

import java.util.StringJoiner;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public final class RestToken implements Token {
    /** SessionToken key. */
    private final String key;

    /** SessionToken creation time. */
    private final long creationTime;

    /** SessionToken scope. */
    private final TokenScope scope;

    /** SessionToken type. */
    private final String type;

    /** Extended information. */
    private final String extendedInformation;

    public RestToken(String key, long creationTime, TokenScope scope, String extendedInformation) {
        this.key = key;
        this.creationTime = creationTime;
        this.scope = scope;
        this.type = SecurityConfiguration.TOKEN_TYPE;
        this.extendedInformation = extendedInformation;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final long getKeyCreationTime() {
        return creationTime;
    }

    @Override
    public String getExtendedInformation() {
        return extendedInformation;
    }

    public TokenScope getScope() {
        return scope;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getType() {
        return type;
    }
}
