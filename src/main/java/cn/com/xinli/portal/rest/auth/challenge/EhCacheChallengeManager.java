package cn.com.xinli.portal.rest.auth.challenge;

import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.CertificateNotFoundException;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.configuration.CachingConfiguration;
import cn.com.xinli.rest.Constants;
import cn.com.xinli.rest.auth.SignatureUtil;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EhCache based challenge manager.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
@Service
public class EhCacheChallengeManager implements ChallengeService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhCacheChallengeManager.class);

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
                SecurityConfiguration.CHALLENGE_TTL,
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
        if (logger.isDebugEnabled()) {
            logger.debug("cached element: {}.", element);
        }

        return cha;
    }

    @Override
    public void deleteChallenge(Challenge challenge) {
        challengeCache.remove(challenge.getNonce());
        logger.info("challenge: {} deleted.", challenge);
    }

    @Override
    public Challenge loadChallenge(String nonce) throws ChallengeNotFoundException {
        Element element = challengeCache.get(nonce);
        if (element == null) {
            throw new ChallengeNotFoundException("challenge not found for: " + nonce + ".");
        }
        return (Challenge) element.getObjectValue();
    }

    @Override
    public boolean verify(Challenge challenge, String answer) {
        if (answer == null)
            return false;

        Certificate certificate;
        try {
            certificate = certificateService.loadCertificate(challenge.getClientId());
        } catch (CertificateNotFoundException e) {
            return false;
        }

        String signature = SignatureUtil.sign(
                challenge.getChallenge().getBytes(),
                certificate.getSharedSecret(),
                Constants.DEFAULT_KEY_SPEC);
        return signature.equals(answer);
    }
}
