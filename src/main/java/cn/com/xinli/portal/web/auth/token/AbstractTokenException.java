package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.web.auth.RestAuthException;
import cn.com.xinli.portal.core.PortalError;

/**
 * Abstract Token Exception.
 *
 * <p>Abstract token exception implements {@link TokenContainer}
 * so that when processing token exceptions, the token key failed to
 * pass verification can be retrieved.
 *
 * <p>Project: xpws
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
