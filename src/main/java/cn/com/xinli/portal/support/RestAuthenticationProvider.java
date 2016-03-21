package cn.com.xinli.portal.support;

import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Authentication Provider.
 *
 * <p>This class is a simple adapter which delegates authentications to
 * local providers provided by factory.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Component
public class RestAuthenticationProvider
        implements AuthenticationProvider, InitializingBean, Ordered {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RestAuthenticationProvider.class);

    @Autowired
    private AuthenticationProviderFactory factory;

    private static final int ORDER = -1;

    @Override
    public boolean supports(Class<?> aClass) {
        return AccessAuthentication.class.isAssignableFrom(aClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(factory);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new BadCredentialsException("authentication can not be empty.");
        }

        if (!supports(authentication.getClass())) {
            throw new BadCredentialsException("unsupported authentication.");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("authenticating {}", authentication);
        }

        AccessAuthentication restAccessAuth = (AccessAuthentication) authentication;
        HttpDigestCredentials credentials = restAccessAuth.getCredentials();

        AuthenticationProvider provider = factory.getAuthenticationProvider(credentials);
        return provider.authenticate(authentication);
    }
}
