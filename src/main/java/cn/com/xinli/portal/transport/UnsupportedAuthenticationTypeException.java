package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.transport.huawei.AuthType;

/**
 * Authentication type not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedAuthenticationTypeException extends TransportException {
    public UnsupportedAuthenticationTypeException(AuthType authType) {
        super(TransportError.UNSUPPORTED_AUTHENTICATION, authType.name());
    }
}
