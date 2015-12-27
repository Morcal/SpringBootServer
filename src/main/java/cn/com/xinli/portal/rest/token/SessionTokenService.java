package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.rest.configuration.SecurityConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    /** Log. */
    private static final Log log = LogFactory.getLog(SessionTokenService.class);

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
            Session session = sessionService.getSession(id);
            if (session == null) {
                log.debug("> Session already gone.");
                return false;
            }
        } catch (NumberFormatException e) {
            log.debug("* Invalid session id.");
            return false;
        }

        return true;
    }

    @Override
    protected int getTtl() {
        return SecurityConfiguration.SESSION_TOKEN_TTL;
    }

}
