package cn.com.xinli.portal.auth.challenge;

/**
 * Challenge service.
 *
 * <p>Challenge service provides methods for other services to
 * load or verify challenges.
 *
 * <p>Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public interface ChallengeService {
    /**
     * Load challenge by nonce.
     * @param nonce challenge nonce.
     * @return challenge if found.
     */
    Challenge loadChallenge(String nonce) throws ChallengeNotFoundException;

    /**
     * Verify challenge.
     *
     * <p>Challenges can be used once only, no matter what this function returns,
     * challenge should be removed immediately before function returns.
     *
     * @param challenge challenge to verify.
     * @param answer challenge answer.
     * @return true if answer matches.
     */
    boolean verify(Challenge challenge, String answer);
}
