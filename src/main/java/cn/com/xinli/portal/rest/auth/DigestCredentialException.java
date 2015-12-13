package cn.com.xinli.portal.rest.auth;

import org.springframework.security.core.AuthenticationException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class DigestCredentialException extends AuthenticationException {
    public DigestCredentialException(String msg, Throwable t) {
        super(msg, t);
    }

    public DigestCredentialException(String msg) {
        super(msg);
    }
}
