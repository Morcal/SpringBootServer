package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.web.auth.AuthorizationServer;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.web.util.AddressUtil;
import cn.com.xinli.portal.web.util.SecureRandomStringGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Rest Authorization Server.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/12.
 */
@Service
public class RestAuthorizationServer implements AuthorizationServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RestAuthorizationServer.class);

    @Autowired
    private SecureRandomStringGenerator secureRandomGenerator;

    @Autowired
    private ChallengeManager challengeManager;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Override
    public boolean certificated(String clientId) {
        return certificateService.isCertified(clientId);
    }

    @Override
    public Challenge createChallenge(String clientId, String scope, boolean requireToken, boolean needRefreshToken) {
        String nonce = secureRandomGenerator.generateUniqueRandomString(32),
                challenge = secureRandomGenerator.generateUniqueRandomString(32);

        Challenge cha = challengeManager.createChallenge(nonce, clientId, challenge, scope, requireToken, needRefreshToken);
        if (logger.isDebugEnabled()) {
            logger.debug("challenge created: {}.", cha);
        }

        return cha;
    }

    @Override
    public boolean verifyIp(String realIp, String ip, String remote) {
        if (!serverConfiguration.isAllowNat()) {
            if ((!StringUtils.isEmpty(realIp) || !StringUtils.isEmpty(ip)) &&
                    !AddressUtil.validateIp(realIp, ip, remote)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("IP check failed, real: {} , remote: {} , given: {}.",
                            realIp, remote, ip);
                }
                return false;
            }
        }
        return true;
    }

}
