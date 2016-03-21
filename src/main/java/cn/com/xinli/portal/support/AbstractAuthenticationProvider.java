package cn.com.xinli.portal.support;

import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.RestRole;
import cn.com.xinli.portal.web.auth.token.TokenScope;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

/**
 * Abstract authentication provider.
 * @author zhoupeng, created on 2016/3/20.
 */
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {

    @Override
    public boolean supports(Class<?> aClass) {
        return AccessAuthentication.class.isAssignableFrom(aClass);
    }

    /**
     * Grant role according to scope.
     * @param scope token scope.
     * @param authorities authorities.
     */
    protected void grantRole(TokenScope scope, Collection<GrantedAuthority> authorities) {
        if (scope == null || authorities == null) {
            return;
        }

        switch (scope) {
            case SYSTEM_ADMIN_TOKEN_SCOPE:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + RestRole.ADMIN.name()));
                break;

            case PORTAL_ACCESS_TOKEN_SCOPE:
                authorities.add(new SimpleGrantedAuthority("ROLE_" + RestRole.USER.name()));
                break;

            case PORTAL_CONTEXT_TOKEN_SCOPE:
            case PORTAL_SESSION_TOKEN_SCOPE:
            default:
                break;
        }
    }

}
