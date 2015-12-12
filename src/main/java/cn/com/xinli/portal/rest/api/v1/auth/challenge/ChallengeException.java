package cn.com.xinli.portal.rest.api.v1.auth.challenge;

import org.springframework.security.core.AuthenticationException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeException extends AuthenticationException {
    public ChallengeException(String msg, Throwable t) {
        super(msg, t);
    }

    public ChallengeException(String msg) {
        super(msg);
    }
}
