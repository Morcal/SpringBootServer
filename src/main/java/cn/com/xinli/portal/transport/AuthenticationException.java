package cn.com.xinli.portal.transport;

/**
 * Portal authentication exception.
 *
 * <p>This exception will throw when portal client failed to pass
 * authentication and received error response from remote server.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class AuthenticationException extends PortalProtocolException {
    public AuthenticationException(ProtocolError error, String message) {
        super(error, message);
    }
}
