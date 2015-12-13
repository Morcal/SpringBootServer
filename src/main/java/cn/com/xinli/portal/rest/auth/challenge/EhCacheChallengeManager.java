package cn.com.xinli.portal.rest.auth.challenge;

import cn.com.xinli.portal.rest.auth.Challenge;
import cn.com.xinli.portal.rest.auth.ChallengeException;
import cn.com.xinli.portal.rest.auth.ChallengeManager;
import cn.com.xinli.portal.rest.auth.ChallengeService;
import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
@Service
public class EhCacheChallengeManager implements ChallengeService, ChallengeManager {
    /** Log. */
    private static final Log log = LogFactory.getLog(EhCacheChallengeManager.class);

    @Autowired
    private Ehcache challengeCache;

    private Element createChallengeElement(Challenge challenge) {
        long now = System.currentTimeMillis();
        return new Element(
                challenge.getNonce(),
                challenge,
                CachingConfiguration.EHCACHE_VERSION,
                now,
                now,
                0,
                true,
                CachingConfiguration.CHALLENGE_TTL,
                0,
                now);
    }

    @Override
    public Challenge createChallenge(String nonce,
                                     String clientId,
                                     String challenge,
                                     String response) {
        ChallengeImpl cha = new ChallengeImpl(nonce, clientId, challenge, response);
        Element element = challengeCache.putIfAbsent(createChallengeElement(cha));
        return (Challenge) element.getObjectValue();
    }

    @Override
    public void deleteChallenge(Challenge challenge) {
        log.info("deleting challenge: " + challenge);
        challengeCache.remove(challenge.getNonce());
    }

    @Override
    public Challenge loadChallenge(String nonce) throws ChallengeException {
        Element element = challengeCache.get(nonce);
        if (element == null) {
            throw new ChallengeException("challenge is gone.");
        }
        return (Challenge) element.getObjectValue();
    }

    @Override
    public void evictExpiredChallenges() {
        log.info("evicting expired challenges...");
        challengeCache.evictExpiredElements();
    }
}
