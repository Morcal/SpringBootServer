package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.web.auth.challenge.Challenge;

/**
 * Authorization Server.
 *
 * <p>This authorization server provides basic authorization
 * for REST APIs.
 *
 * <p>It verifies incoming request's client id (app id).
 *
 * <p>It starts a challenge process for a single client authorization process.
 *
 * <p>It also verifies client's ip address if server option configured.
 *
 * <p>Project: xpws
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
    Challenge createChallenge(String clientId,
                              String scope,
                              boolean requireToken,
                              boolean needRefreshToken);

    /**
     * Verify incoming request's ip address.
     *
     * <p>Server only perform ip validation check when
     * configurations enabled "pws.nat.allowed" option to false.
     *
     * @param realIp real ip from nginx.
     * @param ip ip address client claimed.
     * @param remote remote address by socket peer.
     * @return true if client's ip matches it claimed.
     */
    boolean verifyIp(String realIp, String ip, String remote);
}
