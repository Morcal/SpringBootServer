package cn.com.xinli.portal.support;

import cn.com.xinli.portal.web.auth.BadRestCredentialsException;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Authentication provider factory.
 * @author zhoupeng, created on 2016/3/20.
 */
@Component
public class AuthenticationProviderFactory {

    @Autowired
    private UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private AdminAuthenticationProvider adminAuthenticationProvider;

    /**
     * Get authentication provider by HTTP digest credentials.
     * @param credentials credentials.
     * @return authentication provider.
     */
    public AuthenticationProvider getAuthenticationProvider(HttpDigestCredentials credentials)
            throws AuthenticationException {
        String appId = credentials.getAttribute(HttpDigestCredentials.CLIENT_ID);
        if (StringUtils.isEmpty(appId)) {
            throw new BadRestCredentialsException("credentials missing client app id.");
        }

        if (StringUtils.equals("0", appId) || StringUtils.equals("admin", appId)) {
            return adminAuthenticationProvider;
        } else {
            return userAuthenticationProvider;
        }
    }
}
