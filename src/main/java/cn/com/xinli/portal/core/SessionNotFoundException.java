package cn.com.xinli.portal.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Session Not Found exception.
 *
 * <p>This exception will throw when server try to load/locate target
 * session and target session not exists or expired.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Session not exists or already gone.")
public class SessionNotFoundException extends PortalException {
    public SessionNotFoundException(String message) {
        super(PortalError.SESSION_NOT_FOUND, message);
    }

    public SessionNotFoundException(long sessionId) {
        this("id:" + sessionId);
    }
}
