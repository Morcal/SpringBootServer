package cn.com.xinli.portal.rest.auth.challenge;

import org.springframework.security.core.AuthenticationException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeNotFoundException extends AuthenticationException {
    public ChallengeNotFoundException(String msg) {
        super(msg);
    }
}
