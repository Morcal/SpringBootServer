package cn.com.xinli.portal.service;

import cn.com.xinli.portal.auth.token.AbstractTokenService;
import cn.com.xinli.portal.auth.token.TokenScope;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * REST session token service.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/13.
 */
@Service
public class SessionTokenService extends AbstractTokenService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionTokenService.class);

    @Autowired
    private SessionService sessionService;

    @Override
    protected TokenScope getTokenScope() {
        return TokenScope.PORTAL_SESSION_TOKEN_SCOPE;
    }

    @Override
    protected boolean verifyExtendedInformation(String extendedInformation) {
        try {
            long id = Long.parseLong(extendedInformation);
            return sessionService.exists(id);
        } catch (NumberFormatException e) {
            logger.debug("* Invalid session id.");
            return false;
        }
    }

    @Override
    protected int getTokenTtl() {
        return SecurityConfiguration.SESSION_TOKEN_TTL;
    }

}