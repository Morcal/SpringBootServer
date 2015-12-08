package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.util.AbstractTimeLimitedToken;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public class SessionToken extends AbstractTimeLimitedToken {
    /** Associated session id. */
    private final long sessionId;

    public SessionToken(long sessionId, String value, long expire) {
        super(value, expire);
        this.sessionId = sessionId;
    }

    @Override
    public boolean validate() {
        return false;
    }

    public long getSessionId() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "SessionToken{" +
                "sessionId='" + sessionId + '\'' +
                ", token='" + getValue() + '\'' +
                '}';
    }
}
