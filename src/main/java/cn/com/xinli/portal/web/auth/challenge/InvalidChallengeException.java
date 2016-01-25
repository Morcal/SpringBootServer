package cn.com.xinli.portal.web.auth.challenge;

import cn.com.xinli.portal.web.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Invalid Challenge Exception.
 *
 * <p>This exception will throw when incoming challenge response
 * failed verification.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidChallengeException extends RestAuthException {
    public InvalidChallengeException(String message) {
        super(PortalError.INVALID_CHALLENGE_RESPONSE, message);
    }
}
