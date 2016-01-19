package cn.com.xinli.portal.auth.challenge;

import cn.com.xinli.portal.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Invalid Challenge Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidChallengeException extends RestAuthException {
    public InvalidChallengeException(String message) {
        super(PortalError.of("invalid_challenge_response"), message);
    }
}
