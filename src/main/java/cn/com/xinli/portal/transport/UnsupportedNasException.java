package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.nas.NasType;

/**
 * Nas not supported exception.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class UnsupportedNasException extends TransportException {
    public UnsupportedNasException(NasType nasType) {
        super(TransportError.UNSUPPORTED_NAS, nasType.name());
    }
}
