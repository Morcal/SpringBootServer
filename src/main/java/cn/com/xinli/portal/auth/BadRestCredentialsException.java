package cn.com.xinli.portal.auth;

/**
 * Bad REST credentials exception.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2016/1/4.
 */
public class BadRestCredentialsException extends RuntimeException {
    public BadRestCredentialsException(String message) {
        super(message);
    }

    public BadRestCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
