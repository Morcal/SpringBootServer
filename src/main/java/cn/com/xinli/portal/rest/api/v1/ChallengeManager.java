package cn.com.xinli.portal.rest.api.v1;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public interface ChallengeManager {
    void createChallenge(Challenge challenge);

    void updateChallenge(Challenge challenge);

    void deleteChallenge(Challenge challenge);

    void revoke(String clientId);

    void revokeOne(String nonce);

    boolean exists(String nonce);
}
