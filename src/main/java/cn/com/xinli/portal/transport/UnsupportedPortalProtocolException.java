package cn.com.xinli.portal.transport;

/**
 * Portal protocol not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class UnsupportedPortalProtocolException extends PortalProtocolException {
    public UnsupportedPortalProtocolException(String message) {
        super(ProtocolError.UNSUPPORTED_PROTOCOL, message);
    }
}
