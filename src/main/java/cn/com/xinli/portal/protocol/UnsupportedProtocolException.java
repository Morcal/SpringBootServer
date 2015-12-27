package cn.com.xinli.portal.protocol;

/**
 * Portal protocol not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class UnsupportedProtocolException extends ProtocolException {
    public UnsupportedProtocolException(String message) {
        super(message);
    }
}
