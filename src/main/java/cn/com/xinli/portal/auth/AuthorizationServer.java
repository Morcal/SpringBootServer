package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.Session;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * Authorization Server.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
public interface AuthorizationServer {
//    AuthenticationManager getAuthenticationManager();
    /**
     * Generate a new session token.
     * @param session session.
     * @return new session token.
     */
    SessionToken generateSessionToken(Session session);

    /**
     * Generate a new access token.
     * @param clientId client id issued by PWS.
     * @param secret shared secret issued by PWS.
     * @return new access token.
     */
    AccessToken generateAccessToken(String clientId, String secret);

    /**
     * Validate session token.
     * @param token session token.
     * @return true if session token still valid.
     */
    boolean validateSessionToken(String token);

    /**
     * Validate access token.
     * @param token access token.
     * @return true if access token still valid.
     */
    boolean validateAccessToken(String token);
}
