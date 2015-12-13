package cn.com.xinli.portal.rest.auth;

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
