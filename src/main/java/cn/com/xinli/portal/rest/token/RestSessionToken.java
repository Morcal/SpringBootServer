package cn.com.xinli.portal.rest.token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestSessionToken extends AbstractRestToken {
    private final String sessionId;

    public RestSessionToken(String key, String sessionId) {
        super(key, System.currentTimeMillis());
        this.sessionId = sessionId;
    }

    @Override
    public String getExtendedInformation() {
        return sessionId;
    }
}
