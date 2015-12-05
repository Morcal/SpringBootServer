package cn.com.xinli.portal.protocol;

/**
 * Portal protocol exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class PortalProtocolException extends Exception {

    public PortalProtocolException(String message, Throwable cause) {
        super(message, cause);
    }

    public PortalProtocolException(String message) {
        super(message);
    }

    public PortalProtocolException(Throwable cause) {
         super(cause);
    }
}
