package cn.com.xinli.portal.rest.auth.challenge;

import cn.com.xinli.portal.PortalException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidChallengeException extends PortalException {
    public InvalidChallengeException(String message) {
        super(message);
    }
}
