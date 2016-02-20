package cn.com.xinli.portal.web.auth.challenge;

import cn.com.xinli.portal.Constants;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.support.configuration.CachingConfiguration;
import cn.com.xinli.portal.web.util.SignatureUtil;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EhCache based challenge manager.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Service
public class EhCacheChallengeManager implements ChallengeService, ChallengeManager {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhCacheChallengeManager.class);

    @Autowired
    private Ehcache challengeCache;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ServerConfiguration serverConfiguration;

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
                serverConfiguration.getRestConfiguration().getChallengeTtl(),
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
            throw new ChallengeNotFoundException("nonce: " + nonce + ".");
        }
        return (Challenge) element.getObjectValue();
    }

    @Override
    public boolean verify(Challenge challenge, String answer) {
        if (StringUtils.isEmpty(answer)) {
            throw new IllegalArgumentException("challenge answer can not be blank.");
        }

        deleteChallenge(challenge);

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
