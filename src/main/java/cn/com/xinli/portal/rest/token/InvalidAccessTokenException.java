package cn.com.xinli.portal.rest.token;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidAccessTokenException extends AbstractTokenException {
    public InvalidAccessTokenException(String token) {
        super(token);
    }
}
