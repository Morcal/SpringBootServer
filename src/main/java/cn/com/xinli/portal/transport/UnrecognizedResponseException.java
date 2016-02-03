package cn.com.xinli.portal.transport;

/**
 * Unrecognized response exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class UnrecognizedResponseException extends TransportException {
    public UnrecognizedResponseException(String message) {
        super(TransportError.UNRECOGNIZED_RESPONSE, message);
    }
}
