package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.AuthType;

/**
 * Authentication type not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedAuthenticationTypeException extends ProtocolException {
    public UnsupportedAuthenticationTypeException(AuthType authType) {
        super("Unsupported authentication type: " + authType.name());
    }
}
