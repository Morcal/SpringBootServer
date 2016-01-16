package cn.com.xinli.portal.auth.challenge;

import org.springframework.security.core.AuthenticationException;

/**
 * Invalid Challenge Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidChallengeException extends AuthenticationException {
    public InvalidChallengeException(String message) {
        super(message);
    }
}
