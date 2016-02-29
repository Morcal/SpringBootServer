package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * REST session token service.
 *
 * <p>This class implements a session token service.
 * It verifies session token by checking if session id inside token
 * exists, if not, verification finished exceptionally.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/13.
 */
@Service
public class SessionTokenService extends AbstractTokenService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionTokenService.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private Serializer<TokenKey> delimiterTokenKeySerializer;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_SESSION_TOKEN_SCOPE;
    }

    @Override
    protected Serializer<TokenKey> getTokenKeySerializer() {
        return delimiterTokenKeySerializer;
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        try {
            long id = Long.parseLong(extendedInformation);
            return sessionService.exists(id);
        } catch (NumberFormatException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("* Invalid session token");
            }
            return false;
        }
    }

    @Override
    protected int getTokenTtl() {
        return serverConfiguration.getSessionConfiguration().getTokenTtl();
    }

}
