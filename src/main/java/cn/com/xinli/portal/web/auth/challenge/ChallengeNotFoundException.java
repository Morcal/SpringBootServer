package cn.com.xinli.portal.web.auth.challenge;

import cn.com.xinli.portal.web.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Challenge Not Found Exception.
 *
 * <p>This exception will throw when client requests to authentication
 * with a challenge answer but by that time the challenge either not
 * existed or already expired.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeNotFoundException extends RestAuthException {
    public ChallengeNotFoundException(String msg) {
        super(PortalError.CHALLENGE_NOT_FOUND, msg);
    }
}
