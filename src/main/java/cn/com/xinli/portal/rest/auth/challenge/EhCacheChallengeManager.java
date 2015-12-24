package cn.com.xinli.portal.rest.auth.challenge;

import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.rest.Constants;
import cn.com.xinli.portal.rest.auth.SignatureUtil;
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
public class EhCacheChallengeManager implements ChallengeService {
    /** Log. */
    private static final Log log = LogFactory.getLog(EhCacheChallengeManager.class);

    @Autowired
    private Ehcache challengeCache;

    @Autowired
    private CertificateService certificateService;

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
                                     String scope,
                                     boolean requireToken,
                                     boolean needRefreshToken) {
        ChallengeImpl cha = new ChallengeImpl(nonce, clientId, challenge, scope, requireToken, needRefreshToken);
        challengeCache.put(createChallengeElement(cha));
        Element element = challengeCache.get(cha.getNonce());
        assert element != null;
        if (log.isDebugEnabled()) {
            log.debug("> cached element: " + element);
        }

        return cha;
    }

    @Override
    public void deleteChallenge(Challenge challenge) {
        log.info("deleting challenge: " + challenge);
        challengeCache.remove(challenge.getNonce());
    }

    @Override
    public Challenge loadChallenge(String nonce) throws ChallengeNotFoundException {
        Element element = challengeCache.get(nonce);
        if (element == null) {
            throw new ChallengeNotFoundException("challenge not found for:" + nonce + ".");
        }
        return (Challenge) element.getObjectValue();
    }

    @Override
    public boolean verify(Challenge challenge, String answer) {
        if (answer == null)
            return false;

        Certificate certificate = certificateService.loadCertificate(challenge.getClientId());
        String signature = SignatureUtil.sign(
                challenge.getChallenge().getBytes(),
                certificate.getSharedSecret(),
                Constants.DEFAULT_KEY_SPEC);
        return signature.equals(answer);
    }
}
