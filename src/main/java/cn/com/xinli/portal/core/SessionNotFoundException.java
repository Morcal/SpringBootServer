package cn.com.xinli.portal.core;

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
public class SessionNotFoundException extends PortalException {
    public SessionNotFoundException(String message) {
        super(PortalError.SESSION_NOT_FOUND, message);
    }

    public SessionNotFoundException(long sessionId) {
        this("id:" + sessionId);
    }
}
