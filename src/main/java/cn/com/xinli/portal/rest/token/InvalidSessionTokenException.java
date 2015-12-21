package cn.com.xinli.portal.rest.token;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class InvalidSessionTokenException extends AbstractTokenException {
    public InvalidSessionTokenException(String token) {
        super(token);
    }
}
