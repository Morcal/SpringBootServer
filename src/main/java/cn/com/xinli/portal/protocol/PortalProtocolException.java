package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.PortalException;

/**
 * Portal protocol exception.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class PortalProtocolException extends PortalException {
    public PortalProtocolException(String message) {
        super(message);
    }

    public PortalProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}
