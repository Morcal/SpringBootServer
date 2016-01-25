package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.PortalError;

/**
 * Invalid Session Token Exception.
 *
 * <p>This exception will throw when incoming requests failed to
 * pass session token verification.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidSessionTokenException extends AbstractTokenException {
    public InvalidSessionTokenException(String token) {
        super(PortalError.INVALID_SESSION_GRANT, token);
    }
}
