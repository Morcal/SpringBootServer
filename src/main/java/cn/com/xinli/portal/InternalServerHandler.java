package cn.com.xinli.portal;

import cn.com.xinli.portal.protocol.NasNotFoundException;
import cn.com.xinli.portal.protocol.PortalServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * PWS internal portal server handler.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
@Component
final class InternalServerHandler implements PortalServerHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(InternalServerHandler.class);

    @Autowired
    private SessionService sessionService;

    @Override
    public void handleNtfLogout(String ip) {
        try {
            sessionService.removeSession(ip);
        } catch (SessionNotFoundException | SessionOperationException | NasNotFoundException e) {
            logger.error("Failed to remove session, ip: {}", ip, e);
        }
    }
}
