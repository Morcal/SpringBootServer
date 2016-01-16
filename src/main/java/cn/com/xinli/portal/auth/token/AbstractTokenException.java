package cn.com.xinli.portal.auth.token;

import org.springframework.security.core.AuthenticationException;

/**
 * Abstract Token Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class AbstractTokenException extends AuthenticationException {
    private final String token;

    public AbstractTokenException(String token) {
        super("Invalid token: " + token);
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
