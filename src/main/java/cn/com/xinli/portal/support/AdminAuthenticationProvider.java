package cn.com.xinli.portal.support;

import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.ChallengeAuthority;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import cn.com.xinli.portal.web.auth.RestRole;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.challenge.ChallengeService;
import cn.com.xinli.portal.web.auth.token.AdminTokenService;
import cn.com.xinli.portal.web.auth.token.InvalidAccessTokenException;
import cn.com.xinli.portal.web.auth.token.RestToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Administration authentication provider.
 * @author zhoupeng, created on 2016/3/20.
 */
@Component
public class AdminAuthenticationProvider extends AbstractAuthenticationProvider {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AdminAuthenticationProvider.class);

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private AdminTokenService adminTokenService;

    /**
     * Verify access token.
     *
     * @param authentication authentication.
     * @param credentials credentials.
     * @param authorities authorities.
     * @throws BadCredentialsException
     */
    private void verifyAdminToken(AccessAuthentication authentication,
                                  HttpDigestCredentials credentials,
                                  Collection<GrantedAuthority> authorities) {
        /* Credentials contains access token. */
        final String key = credentials.getAttribute(HttpDigestCredentials.ADMIN_TOKEN);
        Token verified = adminTokenService.verifyToken(key);
        if (verified == null) {
            throw new InvalidAccessTokenException(key);
        }

        if (logger.isDebugEnabled())
            logger.debug("Admin token verified.");

        RestToken restToken = (RestToken) verified;
        authentication.setAuthenticated(true);
        authentication.setAccessToken(restToken);

        grantRole(restToken.getScope(), authorities);
    }

    /**
     * Handle challenge.
     *
     * @param credentials credentials.
     * @param authorities authorities.
     * @throws BadCredentialsException
     */
    private void handleNonce(HttpDigestCredentials credentials,
                             Collection<GrantedAuthority> authorities) {
        String nonce = credentials.getAttribute(HttpDigestCredentials.NONCE);
        Challenge challenge = challengeService.loadChallenge(nonce);
        authorities.add(new ChallengeAuthority(challenge.getChallenge()));
        authorities.add(new SimpleGrantedAuthority("ROLE_" + RestRole.PRE_AUTH.name()));
    }

    @Override
    public AccessAuthentication authenticate(Authentication authentication) throws AuthenticationException {

        AccessAuthentication restAccessAuth = (AccessAuthentication) authentication;
        HttpDigestCredentials credentials = restAccessAuth.getCredentials();

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (credentials.containsAdminToken()) {
            verifyAdminToken(restAccessAuth, credentials, authorities);
        }else if (credentials.containsNonce()) {
            handleNonce(credentials, authorities);
        } else {
            throw new BadCredentialsException("invalid digest credentials.");
        }

        AccessAuthentication populate = new AccessAuthentication(
                authorities,
                restAccessAuth.getPrincipal(),
                restAccessAuth.getCredentials());
        populate.setAccessToken(restAccessAuth.getAccessToken());
        populate.setAuthenticated(true);

        return populate;
    }
}
