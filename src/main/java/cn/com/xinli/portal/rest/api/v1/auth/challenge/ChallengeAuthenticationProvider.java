package cn.com.xinli.portal.rest.api.v1.auth.challenge;

import cn.com.xinli.portal.rest.api.v1.auth.DigestCredentialException;
import cn.com.xinli.portal.rest.api.v1.auth.HttpDigestCredentials;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.util.Assert;

/**
 * Challenge Authentication Provider.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class ChallengeAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {

    private static final int ORDER = -1;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private TokenService tokenService;

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
                Token authenticated = tokenService.allocateToken(
                        credentials.getParameter(HttpDigestCredentials.CLIENT_ID));
                authen.getCredentials().setParameter(
                        HttpDigestCredentials.CLIENT_TOKEN, authenticated.getKey());
                authen.setAuthenticated(true);
            }
        } else if (HttpDigestCredentials.containsToken(credentials)) {
            /* Credentials contains access token. */
            Token verified = tokenService.verifyToken(
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
