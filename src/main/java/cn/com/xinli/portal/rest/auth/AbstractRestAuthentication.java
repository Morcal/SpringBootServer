package cn.com.xinli.portal.rest.auth;

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
public abstract class AbstractRestAuthentication extends AbstractAuthenticationToken {
    private final String principal;
    private HttpDigestCredentials credentials;

    private static final AbstractRestAuthentication empty = new AbstractRestAuthentication(null, null, null) {
        @Override
        public boolean isAuthenticated() {
            return false;
        }
    };

    public static AbstractRestAuthentication empty() {
        return empty;
    }

    public AbstractRestAuthentication(Collection<? extends GrantedAuthority> authorities,
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
