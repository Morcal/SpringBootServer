package cn.com.xinli.portal.core;

/**
 * Invalid credentials exception.
 *
 * <p>This exception will throw, when credentials failed to
 * pass protocol credentials verification before portal requests
 * are processed.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class CredentialsException extends PortalException {
    public CredentialsException(String message) {
        super(PortalError.INVALID_CREDENTIALS, message);
    }

    public CredentialsException(Credentials credentials) {
        super(PortalError.INVALID_CREDENTIALS, credentials.toString());
    }
}
