package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.web.auth.token.RestToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
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
public class AccessAuthentication extends AbstractAuthenticationToken {
    /** Authentication principal. */
    private final String principal;

    /** Authentication credentials. */
    private HttpDigestCredentials credentials;

    /** Associated session token. */
    private RestToken sessionToken;

    /** Associated access token. */
    private RestToken accessToken;

    public AccessAuthentication(Collection<? extends GrantedAuthority> authorities,
                                String principal, HttpDigestCredentials credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
    }

    public AccessAuthentication(String principal, HttpDigestCredentials credentials) {
        this(AuthorityUtils.NO_AUTHORITIES, principal, credentials);
    }

    private static final AccessAuthentication EMPTY =
            new AccessAuthentication(AuthorityUtils.NO_AUTHORITIES, null, null);

    public void setAccessToken(RestToken accessToken) {
        this.accessToken = accessToken;
    }

    public void setSessionToken(RestToken sessionSessionToken) {
        this.sessionToken = sessionSessionToken;
    }

    public static AccessAuthentication empty() {
        return EMPTY;
    }

    /**
     * Get session token.
     * @return session token.
     */
    public RestToken getSessionToken() {
        return sessionToken;
    }

    /**
     * Get access token.
     * @return access token.
     */
    public RestToken getAccessToken() {
        return accessToken;
    }

    @Override
    public HttpDigestCredentials getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }
}
