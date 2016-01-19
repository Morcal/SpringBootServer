package cn.com.xinli.portal.auth.token;

import cn.com.xinli.portal.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Abstract Token Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class AbstractTokenException extends RestAuthException implements TokenContainer {
    /** Invalid token key. */
    private final String token;

    public AbstractTokenException(PortalError error, String token) {
        super(error, "Invalid token: " + token);
        this.token = token;
    }

    @Override
    public String getToken() {
        return token;
    }
}
