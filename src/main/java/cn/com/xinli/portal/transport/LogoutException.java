package cn.com.xinli.portal.transport;

/**
 * Portal logout exception.
 *
 * <p>This exception will throw when portal clients failed to logout
 * on the remote server and received an error response from remote server.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class LogoutException extends TransportException {
    public LogoutException(TransportError error, String message) {
        super(error, message);
    }
}
