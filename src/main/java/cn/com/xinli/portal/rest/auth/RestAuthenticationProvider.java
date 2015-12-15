package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.auth.challenge.ChallengeAuthentication;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Challenge Authentication Provider.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
@Component
public class RestAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {

    private static final int ORDER = -1;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private TokenService accessTokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new IllegalArgumentException("authentication can not be empty.");
        }

        if (!supports(authentication.getClass())) {
            throw new IllegalArgumentException("unsupported authentication.");
        }

        ChallengeAuthentication authen = ChallengeAuthentication.class.cast(authentication);
        HttpDigestCredentials credentials = authen.getCredentials();

        if (HttpDigestCredentials.containsChallenge(credentials)) {
            /* Credentials contains challenge, authenticate it. */
            String nonce = credentials.getParameter(HttpDigestCredentials.NONCE);
            Challenge challenge = challengeService.loadChallenge(nonce);
            if (challenge.equals(authen)) {
                /* Authenticated, allocate a token. */
                authen.setAuthenticated(true);
                /* TODO Provider better information, like Username for token allocation. */
                Token authenticated = accessTokenService.allocateToken(
                        credentials.getParameter(HttpDigestCredentials.CLIENT_ID));
                authen.getCredentials().setParameter(
                        HttpDigestCredentials.CLIENT_TOKEN, authenticated.getKey());
                authen.setAuthenticated(true);
            }
        } else if (HttpDigestCredentials.containsToken(credentials)) {
            /* Credentials contains access token. */
            Token verified = accessTokenService.verifyToken(
                    credentials.getParameter(HttpDigestCredentials.CLIENT_TOKEN));
            if (verified == null) {
                throw new BadCredentialsException("invalid client token.");
            }
        } else {
            throw new DigestCredentialException("invalid digest credentials.");
        }

        return authen;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return ChallengeAuthentication.class.isAssignableFrom(aClass);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(challengeService);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
