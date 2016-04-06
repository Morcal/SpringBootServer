package cn.com.xinli.portal.core.radius;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;

/**
 * RADIUS server not found exception.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public class RadiusNotFoundException extends ServerException {
    public RadiusNotFoundException(String message) {
        super(PortalError.RADIUS_NOT_FOUND, message);
    }
}
