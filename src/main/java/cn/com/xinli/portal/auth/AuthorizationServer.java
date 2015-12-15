package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.rest.auth.Challenge;
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
    Challenge createChallenge(String clientId);

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


//    AuthenticationManager getAuthenticationManager();
//    /**
//     * Generate a new session token.
//     * @param session session.
//     * @return new session token.
//     */
//    SessionToken generateSessionToken(Session session);
//
//    /**
//     * Generate a new access token.
//     * @param clientId client id issued by PWS.
//     * @param secret shared secret issued by PWS.
//     * @return new access token.
//     */
//    AccessToken generateAccessToken(String clientId, String secret);
//
//    /**
//     * Validate session token.
//     * @param token session token.
//     * @return true if session token still valid.
//     */
//    boolean validateSessionToken(String token);
//
//    /**
//     * Validate access token.
//     * @param token access token.
//     * @return true if access token still valid.
//     */
//    boolean validateAccessToken(String token);
}
