package cn.com.xinli.portal.rest;

/**
 * Portal web server REST exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class PortalRestException extends Exception {
    public PortalRestException(String message, Throwable cause) {
        super(message, cause);
    }
}
