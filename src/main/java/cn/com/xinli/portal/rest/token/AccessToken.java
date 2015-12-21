package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class AccessToken extends AbstractToken {
    private final String clientId;

    public AccessToken(String key, String clientId) {
        super(key, System.currentTimeMillis(),
                SecurityConfiguration.PORTAL_USER_ROLE,
                SecurityConfiguration.TOKEN_TYPE);
        this.clientId = clientId;
    }

    @Override
    public String getExtendedInformation() {
        return clientId;
    }
}
