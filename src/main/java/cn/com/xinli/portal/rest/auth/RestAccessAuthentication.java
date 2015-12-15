package cn.com.xinli.portal.rest.auth;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
public class RestAccessAuthentication extends AbstractRestAuthentication {
    public RestAccessAuthentication(Collection<? extends GrantedAuthority> authorities, String principal, HttpDigestCredentials credentials) {
        super(authorities, principal, credentials);
    }
}
