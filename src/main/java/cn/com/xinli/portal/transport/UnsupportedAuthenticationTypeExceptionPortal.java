package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.AuthType;

/**
 * Authentication type not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedAuthenticationTypeExceptionPortal extends PortalProtocolException {
    public UnsupportedAuthenticationTypeExceptionPortal(AuthType authType) {
        super(ProtocolError.UNSUPPORTED_AUTHENTICATION, authType.name());
    }
}
