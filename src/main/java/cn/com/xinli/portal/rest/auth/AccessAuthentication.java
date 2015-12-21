package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.token.AccessToken;
import cn.com.xinli.portal.rest.token.SessionToken;
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
    private SessionToken sessionToken;

    /** Associated access token. */
    private AccessToken accessToken;

    public AccessAuthentication(Collection<? extends GrantedAuthority> authorities,
                                String principal, HttpDigestCredentials credentials) {
        super(authorities, principal, credentials);
    }

    public AccessAuthentication(String principal, HttpDigestCredentials credentials) {
        this(AuthorityUtils.NO_AUTHORITIES, principal, credentials);
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setSessionToken(SessionToken sessionSessionToken) {
        this.sessionToken = sessionSessionToken;
    }

    public SessionToken getSessionToken() {
        return sessionToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }
}
