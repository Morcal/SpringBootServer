package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;

/**
 * NAS not found exception.
 *
 * <p>This exception will throw when can't find NAS for incoming
 * session requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class NasNotFoundException extends PortalException {
    public NasNotFoundException(long id) {
        this("id:" + id);
    }

    public NasNotFoundException(String message) {
        super(PortalError.NAS_NOT_FOUND, message);
    }
}
