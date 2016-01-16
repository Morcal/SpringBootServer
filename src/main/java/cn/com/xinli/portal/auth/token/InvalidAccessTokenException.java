package cn.com.xinli.portal.auth.token;

/**
 * Invalid Access Token Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidAccessTokenException extends AbstractTokenException {
    public InvalidAccessTokenException(String token) {
        super(token);
    }
}
