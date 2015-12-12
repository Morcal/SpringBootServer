package cn.com.xinli.portal.rest.api.v1.auth.challenge;

import cn.com.xinli.portal.rest.api.v1.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public interface ChallengeService {
    /**
     * Load challenge by nonce.
     * @param nonce challenge nonce.
     * @return challenge if found.
     */
    Challenge loadChallenge(String nonce) throws ChallengeException;

    void evictExpiredChallenges();
}
