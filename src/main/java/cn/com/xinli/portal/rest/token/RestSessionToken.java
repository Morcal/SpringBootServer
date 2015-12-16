package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.RestSecurityConfiguration;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestSessionToken extends AbstractRestToken {
    private final String sessionId;

    public RestSessionToken(String key, String sessionId) {
        super(key, System.currentTimeMillis(),
                RestSecurityConfiguration.SESSION_TOKEN_SCOPE,
                RestSecurityConfiguration.TOKEN_TYPE);
        this.sessionId = sessionId;
    }

    @Override
    public String getExtendedInformation() {
        return sessionId;
    }
}
