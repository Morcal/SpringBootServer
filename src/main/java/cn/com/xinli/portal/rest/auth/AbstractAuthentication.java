package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.rest.auth.HttpDigestCredentials;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * REST authentication.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public abstract class AbstractAuthentication extends AbstractAuthenticationToken {
    /** Authentication principal. */
    private final String principal;

    /** Authentication credentials. */
    private HttpDigestCredentials credentials;

    private static final AbstractAuthentication empty = new AbstractAuthentication(null, null, null) {
        @Override
        public boolean isAuthenticated() {
            return false;
        }
    };

    public static AbstractAuthentication empty() {
        return empty;
    }

    public AbstractAuthentication(Collection<? extends GrantedAuthority> authorities,
                                  String principal,
                                  HttpDigestCredentials credentials) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
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
