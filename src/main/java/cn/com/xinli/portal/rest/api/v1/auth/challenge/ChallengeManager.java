package cn.com.xinli.portal.rest.api.v1.auth.challenge;

import cn.com.xinli.portal.rest.api.v1.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public interface ChallengeManager {
    /**
     * Create a challenge.
     * @param nonce nonce.
     * @param clientId client id.
     * @param challenge challenge.
     * @param response challenge response.
     * @return new challenge.
     */
    Challenge createChallenge(String nonce,
                              String clientId,
                              String challenge,
                              String response);

    /**
     * Delete challenge.
     * @param challenge challenge to delete.
     */
    void deleteChallenge(Challenge challenge) throws ChallengeException;
}
