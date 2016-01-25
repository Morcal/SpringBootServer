package cn.com.xinli.portal.transport;

/**
 * NAS not respond exception.
 *
 * <p>This exception will throw when portal-client send request to
 * NAS/BRAS and does not receive any response.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class NasNotRespondException extends PortalProtocolException {
    public NasNotRespondException(String message) {
        super(ProtocolError.NAS_NOT_RESPOND, message);
    }
}
