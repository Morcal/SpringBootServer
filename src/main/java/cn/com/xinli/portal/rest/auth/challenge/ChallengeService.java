package cn.com.xinli.portal.rest.auth.challenge;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public interface ChallengeService extends ChallengeManager {
    /**
     * Load challenge by nonce.
     * @param nonce challenge nonce.
     * @return challenge if found.
     */
    Challenge loadChallenge(String nonce) throws ChallengeNotFoundException;

    /**
     * Version challenge.
     * @param challenge challenge to verify.
     * @param answer challenge answer.
     * @return true if answer matches.
     */
    boolean verify(Challenge challenge, String answer);
}
