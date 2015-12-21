package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Token;

import javax.security.cert.CertificateException;
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
     * Check if given clientId aka appId been certificated.
     * @param clientId client id aka appId.
     * @return true if client id certificated.
     */
    boolean certificated(String clientId);

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
     * Revoke token.
     * @param token token to revoke.
     * @return true if revoked.
     */
    boolean revokeToken(Token token);

    /**
     * Allocate session token.
     * @param session associated session.
     * @return session token.
     */
    Token allocateToken(Session session);
}
