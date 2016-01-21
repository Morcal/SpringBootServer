package cn.com.xinli.portal.auth.challenge;

/**
 * Challenge manager.
 *
 * <p>Challenge manager is in charge of creating a new unique challenge
 * for client authentication challenge process. When a challenge process
 * finished, (no matter it finished successfully or not), manager should
 * delete that challenge so that it ensures challenges can be used only
 * once.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
public interface ChallengeManager {
    /**
     * Create a challenge.
     * @param nonce nonce.
     * @param clientId client id.
     * @param challenge challenge.
     * @param scope challenge.
     * @param requireToken challenge.
     * @param needRefreshToken challenge.
     * @return new challenge.
     */
    Challenge createChallenge(String nonce,
                              String clientId,
                              String challenge,
                              String scope,
                              boolean requireToken,
                              boolean needRefreshToken);

    /**
     * Delete challenge.
     * @param challenge challenge to delete.
     * @throws ChallengeNotFoundException
     */
    void deleteChallenge(Challenge challenge) throws ChallengeNotFoundException;
}
