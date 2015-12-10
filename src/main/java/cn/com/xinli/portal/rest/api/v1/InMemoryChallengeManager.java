package cn.com.xinli.portal.rest.api.v1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
public class InMemoryChallengeManager implements ChallengeService, ChallengeManager {
    /** Log. */
    private static final Log log = LogFactory.getLog(InMemoryChallengeManager.class);

    /** In memory challenges. */
    private final Map<String, ChallengeImpl> challenges = new ConcurrentHashMap<>();

    @Override
    public void createChallenge(Challenge challenge) {
        challenges.putIfAbsent(challenge.getNonce(), (ChallengeImpl) challenge);
    }

    @Override
    public void updateChallenge(Challenge challenge) {
        challenges.put(challenge.getNonce(), (ChallengeImpl) challenge);
    }

    @Override
    public void deleteChallenge(Challenge challenge) {
        challenges.remove(challenge.getNonce());
    }

    @Override
    public void revoke(String clientId) {
        challenges.values().stream().parallel()
                .filter(ch -> ch.getClientId().equals(clientId))
                .forEach(ch -> ch.setRevoked(true));
    }

    @Override
    public void revokeOne(String nonce) {
        ChallengeImpl challenge = challenges.get(nonce);
        if (challenge != null) {
            challenge.setRevoked(true);
        }
    }

    @Override
    public boolean exists(String nonce) {
        return challenges.containsKey(nonce);
    }

    @Override
    public Challenge loadChallenge(String nonce) throws ChallengeException {
        ChallengeImpl challenge = challenges.get(nonce);
        if (challenge == null) {
            throw new ChallengeException("challenge with nonce: " + nonce + " not found.");
        }
        return challenge;
    }
}
