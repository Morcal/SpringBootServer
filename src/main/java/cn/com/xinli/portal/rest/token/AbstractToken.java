package cn.com.xinli.portal.rest.token;

import org.springframework.security.core.token.Token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public abstract class AbstractToken implements Token {
    /** SessionToken key. */
    private final String key;

    /** SessionToken creation time. */
    private final long creationTime;

    /** SessionToken scope. */
    private final String scope;

    /** SessionToken type. */
    private final String type;

    public AbstractToken(String key, long creationTime, String scope, String type) {
        this.key = key;
        this.creationTime = creationTime;
        this.scope = scope;
        this.type = type;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final long getKeyCreationTime() {
        return creationTime;
    }

    public String getScope() {
        return scope;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public String getType() {
        return type;
    }
}
