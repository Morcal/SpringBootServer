package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.BadRestCredentialsException;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import cn.com.xinli.portal.web.auth.SessionAuthority;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.challenge.ChallengeService;
import cn.com.xinli.portal.web.auth.challenge.InvalidChallengeException;
import cn.com.xinli.portal.web.auth.token.*;
import cn.com.xinli.portal.web.rest.RestRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * User authentication provider.
 * @author zhoupeng, created on 2016/3/20.
 */
@Component
public class UserAuthenticationProvider extends AbstractAuthenticationProvider {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(UserAuthenticationProvider.class);

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Autowired
    private ChallengeService challengeService;
    /**
     * verify client application certificate.
     * @param credentials credentials.
     * @return client application certificate.
     * @throws CertificateNotFoundException
     */
    private Certificate verifyCertificate(HttpDigestCredentials credentials)
            throws CertificateNotFoundException {
        /* Retrieve shared secret. */
        String clientId = credentials.getAttribute(HttpDigestCredentials.CLIENT_ID);

        Certificate certificate = certificateService.loadCertificate(clientId);

        if (logger.isDebugEnabled()) {
            logger.debug("certificate loaded: {}", certificate);
        }

        return certificate;
    }

    /**
     * Verify access token.
     *
     * @param authentication authentication.
     * @param credentials credentials.
     * @param authorities authorities.
     * @throws BadCredentialsException
     */
    private void verifyAccessToken(AccessAuthentication authentication,
                                   HttpDigestCredentials credentials,
                                   Collection<GrantedAuthority> authorities) {
        /* Credentials contains access token. */
        final String key = credentials.getAttribute(HttpDigestCredentials.CLIENT_TOKEN);
        Token verified = accessTokenService.verifyToken(key);
        if (verified == null) {
            throw new InvalidAccessTokenException(key);
        }

        if (logger.isDebugEnabled())
            logger.debug("Access token verified.");

        RestToken restToken = (RestToken) verified;
        authentication.setAuthenticated(true);
        authentication.setAccessToken(restToken);

        grantRole(restToken.getScope(), authorities);
    }

    /**
     * Verify session token.
     *
     * @param authentication authentication.
     * @param credentials credentials.
     * @param authorities authorities.
     * @throws BadCredentialsException
     */
    private void verifySessionToken(AccessAuthentication authentication,
                                    HttpDigestCredentials credentials,
                                    Collection<GrantedAuthority> authorities) {
        String key = credentials.getAttribute(HttpDigestCredentials.SESSION_TOKEN);
        if (StringUtils.isEmpty(key)) {
            throw new BadCredentialsException("Empty session token.");
        }

        Token verified = sessionTokenService.verifyToken(key);
        if (verified == null) {
            throw new InvalidSessionTokenException(key);
        }

        logger.debug("Session token verified.");
        authentication.setSessionToken((RestToken) verified);
        long sessionId = Long.parseLong(verified.getExtendedInformation());
        authorities.add(new SessionAuthority(sessionId));
    }

    /**
     * Handle challenge.
     *
     * @param authentication authentication.
     * @param credentials credentials.
     * @param authorities authorities.
     * @throws BadCredentialsException
     */
    private void handleChallengeResponse(AccessAuthentication authentication,
                                         HttpDigestCredentials credentials,
                                         Collection<GrantedAuthority> authorities) {
        /* Credentials contains challenge, authenticate it. */
        String nonce = credentials.getAttribute(HttpDigestCredentials.NONCE),
                response = credentials.getAttribute(HttpDigestCredentials.RESPONSE);
        Challenge challenge = challengeService.loadChallenge(nonce);

        if (!challengeService.verify(challenge, response)) {
            logger.debug("failed to verify challenge.");
            throw new InvalidChallengeException("Incorrect challenge answer.");
        } else {
            logger.debug("challenge verified.");

            /* TODO Provide better information, like Username for token allocation. */
            if (challenge.requiresToken()) {
                Token token = accessTokenService.allocateToken(
                        credentials.getAttribute(HttpDigestCredentials.CLIENT_ID));
                authentication.setAccessToken((RestToken) token);
            }

            Optional<TokenScope> scope = TokenScope.of(challenge.getScope());
            scope.orElseThrow(() -> new BadRestCredentialsException("unknown scope"));
            grantRole(scope.get(), authorities);
        }
    }

    /**
     * Verify request by checking its integrity and checking if
     * request was originated in allowed time range.
     *
     * Create a REST request from incoming request and then sign it,
     * so that we can verify incoming request with signed one.
     *
     * @param authentication authentication.
     * @throws CertificateNotFoundException
     * @throws BadCredentialsException
     */
    private void verifySignature(AccessAuthentication authentication)
            throws CertificateNotFoundException {
        Certificate certificate = verifyCertificate(authentication.getCredentials());

        /* Sign request and compare with incoming one. */
        RestRequest request = (RestRequest) authentication.getDetails();
        request.sign(certificate.getSharedSecret());

        final String signature
                = authentication.getCredentials().getAttribute(HttpDigestCredentials.SIGNATURE),
                signedSignature
                        = request.getCredentials().getAttribute(HttpDigestCredentials.SIGNATURE);

        if (signature == null) {
            throw new BadCredentialsException("Missing signature.");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Comparing signature, calculated: {{}} , original: {{}}.",
                    signedSignature, signature);
        }

        if (!signedSignature.equals(signature)) {
            throw new BadCredentialsException("Signature verify failed.");
        }

        logger.debug("Request signature verified.");
    }

    @Override
    public AccessAuthentication authenticate(Authentication authentication) throws AuthenticationException {

        AccessAuthentication restAccessAuth = (AccessAuthentication) authentication;
        HttpDigestCredentials credentials = restAccessAuth.getCredentials();

        try {
            verifySignature(restAccessAuth);
        } catch (CertificateNotFoundException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /* Handle challenge/access token first. */
        if (credentials.containsChallengeResponse()) {
            handleChallengeResponse(restAccessAuth, credentials, authorities);
        } else if (credentials.containsAccessToken()) {
            verifyAccessToken(restAccessAuth, credentials, authorities);
        } else {
            throw new BadCredentialsException("invalid digest credentials.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        /* Now we handle session token if present. */
        if (credentials.containsSessionToken()) {
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
}
