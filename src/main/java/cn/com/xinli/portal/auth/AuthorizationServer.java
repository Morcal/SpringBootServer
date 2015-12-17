package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.token.RestSessionToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authorization Server.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
public interface AuthorizationServer {
    /**
     * Create a new challenge.
     * @param clientId client id.
     * @param scope token scope.
     * @param requireToken if client requires token.
     * @param needRefreshToken if client requires refresh token.
     * @return challenge.
     */
    Challenge createChallenge(String clientId, String scope, boolean requireToken, boolean needRefreshToken);

    /**
     * Authenticate incoming http request.
     * @param request http request.
     * @param response http response.
     * @return full populated authentication if success.
     * @throws AuthenticationException
     */
    Authentication authenticate(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    /**
     * Handle unsuccessful authentication.
     * @param request request.
     * @param response response.
     * @param authentication authentication failed to authenticate.
     * @param failed authentication exception.
     */
    void unsuccessfulAuthentication(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication,
                                    AuthenticationException failed);


    /**
     * Handle successful authentication.
     * @param request request.
     * @param response response.
     * @param authResult result.
     */
    void successfulAuthentication(HttpServletRequest request,
                                  HttpServletResponse response,
                                  Authentication authResult);

    /**
     * Revoke session token.
     * @param token session token to revoke.
     * @return true if revoked.
     */
    boolean revokeSessionToken(RestSessionToken token);

    /**
     * Allocate session token.
     * @param session associated session.
     * @return session token.
     */
    RestSessionToken allocateSessionToken(Session session);
}
