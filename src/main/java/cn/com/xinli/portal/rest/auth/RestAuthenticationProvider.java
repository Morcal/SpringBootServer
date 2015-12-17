package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeService;
import cn.com.xinli.portal.rest.token.RestAccessToken;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Authentication Provider.
 *
 * Project: xpws
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

    @Autowired
    private TokenService sessionTokenService;

    /**
     * Handle challenge.
     * @param authentication authentication.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void handleChallenge(RestAccessAuthentication authentication, HttpDigestCredentials credentials) {
            /* Credentials contains challenge, authenticate it. */
        String nonce = credentials.getParameter(HttpDigestCredentials.NONCE, String.class),
                response = credentials.getParameter(HttpDigestCredentials.RESPONSE, String.class);
        Challenge challenge = challengeService.loadChallenge(nonce);

        if (challengeService.verify(challenge, response)) {
            /* Remove challenge immediately. */
            challengeService.deleteChallenge(challenge);

            /* Authenticated, allocate a token. */
            authentication.setAuthenticated(true);
            /* TODO Provide better information, like Username for token allocation. */
            if (challenge.requiresToken()) {
                Token token = accessTokenService.allocateToken(
                        credentials.getParameter(HttpDigestCredentials.CLIENT_ID, String.class));
                authentication.setAccessToken(RestAccessToken.class.cast(token));
            }

            authentication.setAuthenticated(true);
        }
    }

    /**
     * Verify access token.
     * @param authentication authentication.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifyAccessToken(RestAccessAuthentication authentication,
                                   HttpDigestCredentials credentials,
                                   Collection<? super GrantedAuthority> authorities) {
            /* Credentials contains access token. */
        Token verified = accessTokenService.verifyToken(
                credentials.getParameter(HttpDigestCredentials.CLIENT_TOKEN, String.class));
        if (verified == null) {
            throw new BadCredentialsException("invalid client token.");
        }
        authentication.setAuthenticated(true);
        authentication.setAccessToken(RestAccessToken.class.cast(verified));
    }

    /**
     * Verify session token.
     * @param authentication authentication.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifySessionToken(RestAccessAuthentication authentication,
                                    HttpDigestCredentials credentials,
                                    Collection<GrantedAuthority> authorities) {
        Token verified = sessionTokenService.verifyToken(
                credentials.getParameter(HttpDigestCredentials.SESSION_TOKEN, String.class));
        if (verified == null) {
            throw new BadCredentialsException("Invalid session token.");
        }
        authentication.setSessionToken(RestSessionToken.class.cast(verified));
        long sessionId = Long.parseLong(verified.getExtendedInformation());
        authorities.add(new SessionAuthority(sessionId));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new IllegalArgumentException("authentication can not be empty.");
        }

        if (!supports(authentication.getClass())) {
            throw new IllegalArgumentException("unsupported authentication.");
        }

        RestAccessAuthentication restAccessAuth = (RestAccessAuthentication) authentication;
        HttpDigestCredentials credentials = restAccessAuth.getCredentials();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /* Handle challenge/access token first. */
        if (HttpDigestCredentials.containsChallenge(credentials)) {
            handleChallenge(restAccessAuth, credentials);
        } else if (HttpDigestCredentials.containsToken(credentials)) {
            verifyAccessToken(restAccessAuth ,credentials, authorities);
        } else {
            throw new BadCredentialsException("invalid digest credentials.");
        }

        /* Now we handle session token if present. */
        if (HttpDigestCredentials.containsSessionToken(credentials)) {
            verifySessionToken(restAccessAuth, credentials, authorities);
        }

        /* Populate a full authenticated authentication with granted authorities. */
        return new RestAccessAuthentication(authorities, restAccessAuth.getPrincipal(), restAccessAuth.getCredentials());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return RestAccessAuthentication.class.isAssignableFrom(aClass);
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
