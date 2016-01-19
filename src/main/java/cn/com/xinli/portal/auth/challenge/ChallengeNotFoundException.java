package cn.com.xinli.portal.auth.challenge;

import cn.com.xinli.portal.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Challenge Not Found Exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeNotFoundException extends RestAuthException {
    public ChallengeNotFoundException(String msg) {
        super(PortalError.of("challenge_not_found"), msg);
    }
}
