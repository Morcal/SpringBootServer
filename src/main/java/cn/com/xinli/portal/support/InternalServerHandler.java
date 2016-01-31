package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.session.SessionService;
import cn.com.xinli.portal.transport.huawei.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * PWS internal huawei portal server handler.
 *
 *
 * <p>A Portal web server in a HUAWEI protocol based portal service need
 * to receive several portal requests from NAS/BRAS, in these scenarios
 * NAS/BRAS devices are portal protocol clients and Portal web server is
 * a portal protocol server.
 *
 * <p>Incoming NTF_LOGOUT requests are send by NAS/BRAS to notify portal web server
 * that certain users already logout(or forced logout due to inactive for
 * certain amount of time of network activity).
 *
 * <p>This class handles incoming requests in form of portal protocol.
 * By now (version 1.0), it only process incoming NTF_LOGOUT requests
 * from NAS/BRAS. Incoming requests other than NTF_LOGOUT will be simply
 * rejected and a warning will be logged.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
@Component
public class InternalServerHandler implements ServerHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(InternalServerHandler.class);

    @Autowired
    private SessionService sessionService;

    @Override
    public ChallengeError challenge(String ip, int requestId, Collection<String> results) {
        logger.warn("Portal web server does not support remote challenge via portal protocol.");
        return ChallengeError.REJECTED;
    }

    @Override
    public AuthError authenticate(int requestId, Credentials credentials, AuthType authType) throws IOException {
        logger.warn("Portal web server does not support remote authenticate via portal protocol.");
        return AuthError.REJECTED;
    }

    @Override
    public LogoutError logout(Credentials credentials) throws IOException {
        logger.warn("Portal web server does not support remote logout via portal protocol.");
        return LogoutError.REJECTED;
    }

    @Override
    public LogoutError ntfLogout(String nasIp, String userIp) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("NAS NtfLogout, nas:{}, user:{}", nasIp, userIp);
        }

        try {
            sessionService.removeSession(nasIp, userIp);
            if(logger.isDebugEnabled()) {
                logger.debug("NTF_LOGOUT nas:{}, user:{}", nasIp, userIp);
            }
            return LogoutError.OK;
        } catch (NasNotFoundException e) {
            logger.error("Failed to remove session, nas:{}, user:{}", nasIp, userIp, e);
            return LogoutError.FAILED;
        } catch (PortalException e) {
            return LogoutError.GONE;
        }
    }
}