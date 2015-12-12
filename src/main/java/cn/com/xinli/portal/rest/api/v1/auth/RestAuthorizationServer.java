package cn.com.xinli.portal.rest.api.v1.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.rest.api.RestAuthenticationFailureEvent;
import cn.com.xinli.portal.rest.api.v1.SecureKeyGenerator;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeAuthentication;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeImpl;
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
    private SecureKeyGenerator secureKeyGenerator;

    /** Application event publisher. */
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private Optional<String> getAuthenticationFromRequestHeader(HttpServletRequest request) {
        String authentication = request.getHeader(HttpDigestCredentials.HEADER_NAME);
        return Optional.ofNullable(authentication);
    }

    /**
     * Get credentials from request.
     * @param request request.
     * @return credential.
     * @throws BadCredentialsException if request does not contains well-formed credentials.
     */
    protected Optional<HttpDigestCredentials> getCredentials(HttpServletRequest request) {
        Optional<String> o = getAuthenticationFromRequestHeader(request);

        if (!o.isPresent()) {
            return Optional.empty();
        }

        final HttpDigestCredentials credentials = HttpDigestCredentials.of(o.get().trim());
        return Optional.ofNullable(credentials);
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
    public Challenge createChallenge(String clientId) {
        String nonce = secureKeyGenerator.generateUniqueRandomString(),
                challenge = secureKeyGenerator.generateUniqueRandomString();

        String response = "";
        ChallengeImpl impl = new ChallengeImpl(nonce, clientId, challenge, response);
        log.info("challenge created: " + impl);
        return impl;
    }

    @Override
    public void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        if (log.isDebugEnabled()) {
            log.debug("Cleared security context due to exception", failed);
        }

        //TODO remove unnecessary request attribute.
        request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", failed);
        //TODO check if spring-security response on exception.

        applicationEventPublisher.publishEvent(
                new RestAuthenticationFailureEvent(request, response, null, failed));
    }

    @Override
    public Authentication authenticate(HttpServletRequest request, HttpServletResponse response) {
        Optional<HttpDigestCredentials> opt = getCredentials(request);

        if (!opt.isPresent()) {
            throw new BadCredentialsException("Missing credentials.");
        }

        HttpDigestCredentials credentials = opt.get();
        String principal = credentials.getParameter(HttpDigestCredentials.CLIENT_ID);
        if (principal == null) {
            throw new BadCredentialsException("principal not found.");
        }

        ChallengeAuthentication authentication = new ChallengeAuthentication(principal, credentials);
        //TODO add authentication details.
        //failed.setDetails(this.authenticationDetailsSource.buildDetails(request));
        Authentication result = authenticationManager.authenticate(authentication);
        successfulAuthentication(request, response, result);

        return result;
    }
}