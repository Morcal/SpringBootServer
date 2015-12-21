package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeService;
import cn.com.xinli.portal.rest.auth.challenge.InvalidChallengeException;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.token.AccessToken;
import cn.com.xinli.portal.rest.token.InvalidAccessTokenException;
import cn.com.xinli.portal.rest.token.InvalidSessionTokenException;
import cn.com.xinli.portal.rest.token.SessionToken;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthenticationProvider.class);

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
    private void handleChallenge(AccessAuthentication authentication,
                                 HttpDigestCredentials credentials,
                                 Collection<GrantedAuthority> authorities) {
            /* Credentials contains challenge, authenticate it. */
        String nonce = credentials.getParameter(HttpDigestCredentials.NONCE),
                response = credentials.getParameter(HttpDigestCredentials.RESPONSE);
        Challenge challenge = challengeService.loadChallenge(nonce);

        if (!challengeService.verify(challenge, response)) {
            log.debug("> failed to verify challenge.");
            throw new InvalidChallengeException("Incorrect challenge answer.");
        } else {
            log.debug("> challenge verified.");
            /* Remove challenge immediately. */
            challengeService.deleteChallenge(challenge);

            /* Authenticated, allocate a token. */
            authentication.setAuthenticated(true);
            /* TODO Provide better information, like Username for token allocation. */
            if (challenge.requiresToken()) {
                org.springframework.security.core.token.Token token = accessTokenService.allocateToken(
                        credentials.getParameter(HttpDigestCredentials.CLIENT_ID));
                authentication.setAccessToken(AccessToken.class.cast(token));
            }

            authentication.setAuthenticated(true);
            authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.PORTAL_USER_ROLE));
        }
    }

    /**
     * Verify access token.
     * @param authentication authentication.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifyAccessToken(AccessAuthentication authentication,
                                   HttpDigestCredentials credentials,
                                   Collection<GrantedAuthority> authorities) {
        /* Credentials contains access token. */
        org.springframework.security.core.token.Token verified = accessTokenService.verifyToken(
                credentials.getParameter(HttpDigestCredentials.CLIENT_TOKEN));
        if (verified == null) {
            throw new InvalidAccessTokenException("invalid client token.");
        }
        log.debug("> Session token verified.");
        authentication.setAuthenticated(true);
        authentication.setAccessToken(AccessToken.class.cast(verified));
        authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.PORTAL_USER_ROLE));
    }

    /**
     * Verify session token.
     * @param authentication authentication.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifySessionToken(AccessAuthentication authentication,
                                    HttpDigestCredentials credentials,
                                    Collection<GrantedAuthority> authorities) {
        org.springframework.security.core.token.Token verified = sessionTokenService.verifyToken(
                credentials.getParameter(HttpDigestCredentials.SESSION_TOKEN));
        if (verified == null) {
            throw new InvalidSessionTokenException("Invalid session token.");
        }
        authentication.setSessionToken(SessionToken.class.cast(verified));
        long sessionId = Long.parseLong(verified.getExtendedInformation());
        authorities.add(new SessionAuthority(sessionId));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication == null) {
            throw new BadCredentialsException("authentication can not be empty.");
        }

        if (!supports(authentication.getClass())) {
            throw new BadCredentialsException("unsupported authentication.");
        }

        AccessAuthentication restAccessAuth = (AccessAuthentication) authentication;
        HttpDigestCredentials credentials = restAccessAuth.getCredentials();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /* Handle challenge/access token first. */
        if (HttpDigestCredentials.containsChallenge(credentials)) {
            handleChallenge(restAccessAuth, credentials, authorities);
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
        AccessAuthentication populate = new AccessAuthentication(
                authorities,
                restAccessAuth.getPrincipal(),
                restAccessAuth.getCredentials());
        populate.setAccessToken(restAccessAuth.getAccessToken());
        populate.setSessionToken(restAccessAuth.getSessionToken());
        populate.setAuthenticated(true);

        return populate;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return AccessAuthentication.class.isAssignableFrom(aClass);
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
