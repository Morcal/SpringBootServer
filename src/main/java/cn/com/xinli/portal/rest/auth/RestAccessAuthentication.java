package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.token.RestAccessToken;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * REST access authentication.
 *
 * <p>Newly created {@link RestAccessAuthentication} has no any
 * of {@link GrantedAuthority}s.</p>
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class RestAccessAuthentication extends AbstractRestAuthentication {
    /** Associated session token. */
    private RestSessionToken sessionToken;

    /** Associated access token. */
    private RestAccessToken accessToken;

    public RestAccessAuthentication(Collection<? extends GrantedAuthority> authorities,
                                    String principal, HttpDigestCredentials credentials) {
        super(authorities, principal, credentials);
    }

    public RestAccessAuthentication(String principal, HttpDigestCredentials credentials) {
        this(AuthorityUtils.NO_AUTHORITIES, principal, credentials);
    }

    public void setAccessToken(RestAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setSessionToken(RestSessionToken sessionToken) {
        this.sessionToken = sessionToken;
    }

    public RestSessionToken getSessionToken() {
        return sessionToken;
    }

    public RestAccessToken getAccessToken() {
        return accessToken;
    }
}
