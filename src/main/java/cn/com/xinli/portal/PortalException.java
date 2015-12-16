package cn.com.xinli.portal;

/**
 * Portal web server exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class PortalException extends RuntimeException {
    public PortalException(String message) {
        super(message);
    }

    public PortalException(String message, Throwable cause) {
        super(message, cause);
    }
}
