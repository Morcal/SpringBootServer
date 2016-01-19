package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.core.PortalError;

/**
 * Bad REST credentials exception.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2016/1/4.
 */
public class BadRestCredentialsException extends RestAuthException {
    public BadRestCredentialsException(String message) {
        super(PortalError.of("bad_client_credentials"), message);
    }

    public BadRestCredentialsException(String message, Throwable cause) {
        super(PortalError.of("bad_client_credentials"), message, cause);
    }
}
