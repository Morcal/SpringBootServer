package cn.com.xinli.portal.core;

/**
 * Session operation exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class SessionOperationException extends PortalException {
    public SessionOperationException(String message) {
        super(message);
    }

    public SessionOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
