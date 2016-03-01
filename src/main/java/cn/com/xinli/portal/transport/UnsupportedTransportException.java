package cn.com.xinli.portal.transport;

/**
 * Portal protocol not supported exception.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class UnsupportedTransportException extends TransportException {
    public UnsupportedTransportException(String message) {
        super(TransportError.UNSUPPORTED_PROTOCOL, message);
    }
}
