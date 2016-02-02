package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.nas.NasType;

/**
 * Nas not supported exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedNasException extends PortalProtocolException {
    public UnsupportedNasException(NasType nasType) {
        super(ProtocolError.UNSUPPORTED_NAS, nasType.name());
    }
}
