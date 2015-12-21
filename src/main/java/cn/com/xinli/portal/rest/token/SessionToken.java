package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class SessionToken extends AbstractToken {
    private final String sessionId;

    public SessionToken(String key, String sessionId) {
        super(key, System.currentTimeMillis(),
                SecurityConfiguration.SESSION_TOKEN_SCOPE,
                SecurityConfiguration.TOKEN_TYPE);
        this.sessionId = sessionId;
    }

    @Override
    public String getExtendedInformation() {
        return sessionId;
    }

    @Override
    public String toString() {
        return "SessionToken{" +
                "sessionId='" + sessionId + '\'' +
                '}';
    }
}
