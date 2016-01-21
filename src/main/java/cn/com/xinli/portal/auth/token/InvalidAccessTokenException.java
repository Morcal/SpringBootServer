package cn.com.xinli.portal.auth.token;

import cn.com.xinli.portal.core.PortalError;

/**
 * Invalid Access Token Exception.
 *
 * <p>This exception will throw when incoming requests failed to
 * pass access token verification.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidAccessTokenException extends AbstractTokenException {
    public InvalidAccessTokenException(String token) {
        super(PortalError.of("invalid_client_grant"), token);
    }
}
