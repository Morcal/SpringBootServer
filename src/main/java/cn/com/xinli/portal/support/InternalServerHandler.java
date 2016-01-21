package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.NasNotFoundException;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.SessionManager;
import cn.com.xinli.portal.protocol.PortalServerHandler;
import cn.com.xinli.portal.protocol.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PWS internal portal server handler.
 *
 * <p>This class handles incoming request in form of portal protocol.
 * By now (version 1.0), it only process incoming NTF_LOGOUT requests
 * from NAS/BRAS.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
@Component
public class InternalServerHandler implements PortalServerHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(InternalServerHandler.class);

    @Autowired
    private SessionManager sessionManager;

    @Override
    public int handleNtfLogout(String ip) {
        if (logger.isTraceEnabled()) {
            logger.trace("NAS NtfLogout, ip: {}.", ip);
        }

        try {
            //FIXME remove session directly may drag down server performance.
            Result result = sessionManager.removeSession(ip);
            if(logger.isDebugEnabled()) {
                logger.debug("NTF_LOGOUT {}", result);
            }
            return 0; /* LogoutError.OK */
        } catch (NasNotFoundException e) {
            logger.error("Failed to remove session, ip: {}", ip, e);
            return 0x02; /* LogoutError.FAILED. */
        } catch (PortalException e) {
            return 0x03; /* LogoutError.GONE. */
        }
    }
}