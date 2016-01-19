package cn.com.xinli.portal.auth.token;

import cn.com.xinli.portal.core.PortalError;

/**
 * Invalid Session Token Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidSessionTokenException extends AbstractTokenException {
    public InvalidSessionTokenException(String token) {
        super(PortalError.of("invalid_session_grant"), token);
    }
}
