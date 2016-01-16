package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.auth.token.RestToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

/**
 * REST access authentication.
 *
 * <p>Newly created {@link AccessAuthentication} has no any
 * of {@link GrantedAuthority}s.</p>
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class AccessAuthentication extends AbstractAuthentication {
    /** Associated session token. */
    private RestToken sessionToken;

    /** Associated access token. */
    private RestToken accessToken;

    public AccessAuthentication(Collection<? extends GrantedAuthority> authorities,
                                String principal, HttpDigestCredentials credentials) {
        super(authorities, principal, credentials);
    }

    public AccessAuthentication(String principal, HttpDigestCredentials credentials) {
        this(AuthorityUtils.NO_AUTHORITIES, principal, credentials);
    }

    public void setAccessToken(RestToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setSessionToken(RestToken sessionSessionToken) {
        this.sessionToken = sessionSessionToken;
    }

    public RestToken getSessionToken() {
        return sessionToken;
    }

    public RestToken getAccessToken() {
        return accessToken;
    }
}
