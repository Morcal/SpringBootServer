package cn.com.xinli.portal.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public class AccessToken extends AbstractAuthenticationToken {
    private final Object principal;
    private Object credentials;

    public AccessToken(String principal, String response) {
        super(null);
        this.principal = principal;
        this.credentials = response;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(null, null);
        return null;
    }
}
