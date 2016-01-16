package cn.com.xinli.portal.core;

/**
 * Portal web server exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public abstract class PortalException extends Exception {
    public PortalException(String message) {
        super(message);
    }

    public PortalException(String message, Throwable cause) {
        super(message, cause);
    }
}
