package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import cn.com.xinli.portal.rest.token.SessionTokenService;
import cn.com.xinli.portal.util.RandomStringGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.token.Token;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestAuthorizationServer implements AuthorizationServer {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestAuthorizationServer.class);

    @Autowired
    private RandomStringGenerator secureRandomGenerator;

    @Autowired
    private ChallengeManager challengeManager;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private SessionTokenService sessionTokenService;

    @Override
    @PreAuthorize(SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE)
    public boolean revokeToken(Token token) {
        return sessionTokenService.revokeToken(token);
    }

    @Override
    @PreAuthorize(SecurityConfiguration.SPRING_EL_PORTAL_USER_ROLE)
    public Token allocateToken(Session session) {
        return sessionTokenService.allocateToken(String.valueOf(session.getId()));
    }

    @Override
    public boolean certificated(String clientId) {
        return certificateService.isCertified(clientId);
    }

    @Override
    public Challenge createChallenge(String clientId, String scope, boolean requireToken, boolean needRefreshToken) {
        String nonce = secureRandomGenerator.generateUniqueRandomString(),
                challenge = secureRandomGenerator.generateUniqueRandomString();

        Challenge cha = challengeManager.createChallenge(nonce, clientId, challenge, scope, requireToken, needRefreshToken);
        log.info("challenge created: " + cha);
        return cha;
    }

}
