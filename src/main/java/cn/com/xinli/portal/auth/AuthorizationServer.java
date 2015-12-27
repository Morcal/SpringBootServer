package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.rest.auth.challenge.Challenge;

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

}
