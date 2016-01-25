package cn.com.xinli.portal.web.auth;

import cn.com.xinli.portal.core.PortalError;

/**
 * Bad REST credentials exception.
 *
 * <p>This exception will throw if incoming request's credentials
 * is not well formed.
 *
 * <p>Project: xpws.
 *
 * @author zhoupeng 2016/1/4.
 */
public class BadRestCredentialsException extends RestAuthException {
    public BadRestCredentialsException() {
        super(PortalError.BAD_CLIENT_CREDENTIALS);
    }

    public BadRestCredentialsException(String message) {
        super(PortalError.BAD_CLIENT_CREDENTIALS, message);
    }

    public BadRestCredentialsException(String message, Throwable cause) {
        super(PortalError.BAD_CLIENT_CREDENTIALS, message, cause);
    }
}
