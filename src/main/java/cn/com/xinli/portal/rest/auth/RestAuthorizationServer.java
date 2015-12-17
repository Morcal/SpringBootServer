package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.CredentialsUtil;
import cn.com.xinli.portal.rest.RandomStringGenerator;
import cn.com.xinli.portal.rest.RestAuthenticationFailureEvent;
import cn.com.xinli.portal.rest.RestRequestSupport;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.rest.configuration.RestSecurityConfiguration;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import cn.com.xinli.portal.rest.token.RestSessionTokenService;
import cn.com.xinli.portal.rest.token.TokenManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestAuthorizationServer implements AuthorizationServer, ApplicationEventPublisherAware  {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthorizationServer.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RandomStringGenerator secureRandomGenerator;

    @Autowired
    private ChallengeManager challengeManager;

    @Autowired
    private RestSessionTokenService sessionTokenService;

    @Autowired
    private ServerConfig serverConfig;

    /** Application event publisher. */
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authResult) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication success: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(
                    new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
        }
    }

    @Override
    public boolean revokeSessionToken(RestSessionToken token) {
        return sessionTokenService.revokeToken(token);
    }

    @Override
    public RestSessionToken allocateSessionToken(Session session) {
        return (RestSessionToken) sessionTokenService.allocateToken(String.valueOf(session.getId()));
    }

    @Override
    public Challenge createChallenge(String clientId, String scope, boolean requireToken, boolean needRefreshToken) {
        String nonce = secureRandomGenerator.generateUniqueRandomString(),
                challenge = secureRandomGenerator.generateUniqueRandomString();

        Challenge chal = challengeManager.createChallenge(nonce, clientId, challenge, scope, requireToken, needRefreshToken);
        log.info("challenge created: " + chal);
        return chal;
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Authentication authentication,
                                           AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Cleared security context due to exception", failed);
        }

        //TODO remove unnecessary request attribute.
        request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", failed);
        //TODO check if spring-security response on exception.

        applicationEventPublisher.publishEvent(
                new RestAuthenticationFailureEvent(request, response, authentication, failed));
    }

    /**
     * Verify request by checking its integrity and checking if
     * request was originated in allowed time range.
     *
     * @param request http request.
     * @param credentials credentials.
     * @throws BadCredentialsException
     */
    private void verifyRequest(HttpServletRequest request, HttpDigestCredentials credentials) {
        long now = System.currentTimeMillis();
        long timestamp = credentials.getParameter(HttpDigestCredentials.TIMESTAMP, Long.class);

        long diff = Math.abs(now - timestamp);

        if (diff > RestSecurityConfiguration.MAX_TIME_DIFF) {
            throw new BadCredentialsException("Way too inaccurate timestamp.");
        }

        /*
         * Create a REST request from incoming request and then sign it,
         * so that we can verify incoming request with signed one.
         */
        RestRequestSupport support = new RestRequestSupport();
        request.getParameterMap().forEach((s, strings) -> {
            for (String string : strings) {
                support.setParameter(s, string);
            }
        });
        credentials.getParameters().forEach((s, o) -> support.getCredentials().setParameter(s, o));
        support.sign(request.getMethod(), request.getRequestURI(), serverConfig.getPrivateKey());

        String signature = credentials.getParameter(HttpDigestCredentials.SIGNATURE, String.class);
        String signedSignature = support.getCredentials().getParameter(HttpDigestCredentials.SIGNATURE, String.class);

        if (signature == null) {
            throw new BadCredentialsException("Missing signature.");
        }

        if (!signedSignature.equals(signature)) {
            throw new BadCredentialsException("Signature verify failed.");
        }
    }

    @Override
    public Authentication authenticate(HttpServletRequest request, HttpServletResponse response) {
        Optional<HttpDigestCredentials> opt = CredentialsUtil.getCredentials(request);

        if (!opt.isPresent()) {
            throw new BadCredentialsException("Missing credentials.");
        }

        HttpDigestCredentials credentials = opt.get();
        String principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID, String.class);
        if (principal == null) {
            throw new BadCredentialsException("principal not found.");
        }

        /* Verify request. */
        verifyRequest(request, credentials);

        RestAccessAuthentication authentication = new RestAccessAuthentication(principal, credentials);
        //TODO add authentication details.
        //failed.setDetails(this.authenticationDetailsSource.buildDetails(request));
        Authentication result = authenticationManager.authenticate(authentication);
        successfulAuthentication(request, response, result);

        return result;
    }
}
