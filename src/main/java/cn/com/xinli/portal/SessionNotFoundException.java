package cn.com.xinli.portal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Session Not Found exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/16.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Session not exists or already gone.")
public class SessionNotFoundException extends PortalException {
    public SessionNotFoundException(long sessionId) {
        super("Session: " + sessionId + " not found.");
    }

    public SessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
