package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.util.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
@Service
public class RestAuthorizationServer implements AuthorizationServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RestAuthorizationServer.class);

    @Autowired
    private RandomStringGenerator secureRandomGenerator;

    @Autowired
    private ChallengeManager challengeManager;

    @Autowired
    private CertificateService certificateService;

    @Override
    public boolean certificated(String clientId) {
        return certificateService.isCertified(clientId);
    }

    @Override
    public Challenge createChallenge(String clientId, String scope, boolean requireToken, boolean needRefreshToken) {
        String nonce = secureRandomGenerator.generateUniqueRandomString(32),
                challenge = secureRandomGenerator.generateUniqueRandomString(32);

        Challenge cha = challengeManager.createChallenge(nonce, clientId, challenge, scope, requireToken, needRefreshToken);
        logger.info("challenge created: " + cha);
        return cha;
    }

}
