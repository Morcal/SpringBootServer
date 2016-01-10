package cn.com.xinli.portal;

import cn.com.xinli.portal.protocol.Message;
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
class InternalServerHandler implements PortalServerHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(InternalServerHandler.class);

    @Autowired
    private SessionService sessionService;

    @Override
    public int handleNtfLogout(String ip) {
        if (logger.isTraceEnabled()) {
            logger.trace("NAS NtfLogout, ip: {}.", ip);
        }

        try {
            Message message = sessionService.removeSession(ip);
            return message.isSuccess() ? 0 /* LogoutError.OK */ : 0x02 /* LogoutError.FAILED. */;
        } catch (SessionNotFoundException e) {
            return 0x03; /* LogoutError.GONE. */
        } catch (SessionOperationException | NasNotFoundException e) {
            logger.error("Failed to remove session, ip: {}", ip, e);
            return 0x02; /* LogoutError.FAILED. */
        }
    }
}