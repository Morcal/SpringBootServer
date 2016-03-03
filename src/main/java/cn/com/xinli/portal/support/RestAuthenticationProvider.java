package cn.com.xinli.portal.support;

import cn.com.xinli.portal.web.auth.AccessAuthentication;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import cn.com.xinli.portal.web.auth.RestRole;
import cn.com.xinli.portal.web.auth.SessionAuthority;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.challenge.ChallengeService;
import cn.com.xinli.portal.web.auth.challenge.InvalidChallengeException;
import cn.com.xinli.portal.web.auth.token.*;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.web.rest.RestRequest;
import cn.com.xinli.portal.web.auth.token.AccessTokenService;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.web.auth.token.SessionTokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Authentication Provider.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Component
public class RestAuthenticationProvider implements AuthenticationProvider, InitializingBean, Ordered {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RestAuthenticationProvider.class);

    private static final int ORDER = -1;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private SessionTokenService sessionTokenService;

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

    private Certificate verifyCertificate(HttpDigestCredentials credentials) throws CertificateNotFoundException {
        /* Retrieve shared secret. */
        String clientId = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);

        Certificate certificate = certificateService.loadCertificate(clientId);

        if (logger.isDebugEnabled()) {
            logger.debug("certificate loaded: {}", certificate);
        }

        return certificate;
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
    private void verifySignature(AccessAuthentication authentication) throws CertificateNotFoundException {
        Certificate certificate = verifyCertificate(authentication.getCredentials());

        /* Sign request and compare with incoming one. */
        RestRequest request = (RestRequest) authentication.getDetails();
        request.sign(certificate.getSharedSecret());

        String signature = authentication.getCredentials().getParameter(HttpDigestCredentials.SIGNATURE);
        String signedSignature = request.getCredentials().getParameter(HttpDigestCredentials.SIGNATURE);

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
            logger.debug("failed to verify challenge.");
            throw new InvalidChallengeException("Incorrect challenge answer.");
        } else {
            logger.debug("challenge verified.");

            /* TODO Provide better information, like Username for token allocation. */
            if (challenge.requiresToken()) {
                Token token = accessTokenService.allocateToken(
                        credentials.getParameter(HttpDigestCredentials.CLIENT_ID));
                authentication.setAccessToken((RestToken) token);
            }

            authorities.add(new SimpleGrantedAuthority("ROLE_" + RestRole.USER.name()));
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
        final String key = credentials.getParameter(HttpDigestCredentials.CLIENT_TOKEN);
        Token verified = accessTokenService.verifyToken(key);
        if (verified == null) {
            throw new InvalidAccessTokenException(key);
        }
        logger.debug("Access token verified.");
        authentication.setAuthenticated(true);
        authentication.setAccessToken((RestToken) verified);
        authorities.add(new SimpleGrantedAuthority("ROLE_" + RestRole.USER.name()));
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
        String key = credentials.getParameter(HttpDigestCredentials.SESSION_TOKEN);
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

        try {
            verifySignature(restAccessAuth);
        } catch (CertificateNotFoundException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /* Handle challenge/access token first. */
        if (HttpDigestCredentials.containsChallenge(credentials)) {
            handleChallenge(restAccessAuth, credentials, authorities);
        } else if (HttpDigestCredentials.containsAccessToken(credentials)) {
            verifyAccessToken(restAccessAuth ,credentials, authorities);
        } else {
            throw new BadCredentialsException("invalid digest credentials.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

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
}
