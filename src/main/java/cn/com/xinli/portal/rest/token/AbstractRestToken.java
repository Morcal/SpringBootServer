package cn.com.xinli.portal.rest.token;

import org.springframework.security.core.token.Token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public abstract class AbstractRestToken implements Token {
    /** Token key. */
    private final String key;

    /** Token creation time. */
    private final long creationTime;

    public AbstractRestToken(String key, long creationTime) {
        this.key = key;
        this.creationTime = creationTime;
    }

    @Override
    public final String getKey() {
        return key;
    }

    @Override
    public final long getKeyCreationTime() {
        return creationTime;
    }
}
