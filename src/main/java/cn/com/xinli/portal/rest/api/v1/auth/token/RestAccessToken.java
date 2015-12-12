package cn.com.xinli.portal.rest.api.v1.auth.token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestAccessToken extends AbstractRestToken {
    private final String clientId;

    public RestAccessToken(String key, String clientId) {
        super(key, System.currentTimeMillis());
        this.clientId = clientId;
    }

    @Override
    public String getExtendedInformation() {
        return clientId;
    }
}
