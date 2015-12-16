package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.RestSecurityConfiguration;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestAccessToken extends AbstractRestToken {
    private final String clientId;

    public RestAccessToken(String key, String clientId) {
        super(key, System.currentTimeMillis(),
                RestSecurityConfiguration.ACCESS_TOKEN_SCOPE,
                RestSecurityConfiguration.TOKEN_TYPE);
        this.clientId = clientId;
    }

    @Override
    public String getExtendedInformation() {
        return clientId;
    }
}
