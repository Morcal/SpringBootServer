package cn.com.xinli.portal.transport;

/**
 * Unrecognized response exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class UnrecognizedResponseException extends PortalProtocolException {
    public UnrecognizedResponseException(String message) {
        super(ProtocolError.UNRECOGNIZED_RESPONSE, message);
    }
}
