package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.PortalException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class ProtocolNotSupportedException extends PortalException {
    public ProtocolNotSupportedException(String message) {
        super(message);
    }
}
