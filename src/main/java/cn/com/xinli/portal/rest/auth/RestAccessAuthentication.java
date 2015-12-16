package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.token.RestAccessToken;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class RestAccessAuthentication extends AbstractRestAuthentication {
    private RestSessionToken sessionToken;
    private RestAccessToken accessToken;

    public RestAccessAuthentication(Collection<? extends GrantedAuthority> authorities, String principal, HttpDigestCredentials credentials) {
        super(authorities, principal, credentials);
    }

    public RestSessionToken getSessionToken() {
        return sessionToken;
    }

    public RestAccessToken getAccessToken() {
        return accessToken;
    }
}
