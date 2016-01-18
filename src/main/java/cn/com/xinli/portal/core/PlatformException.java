package cn.com.xinli.portal.core;

import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.support.PortalErrorTranslator;

/**
 * Portal platform (including NAS/BRAS, AAA platform) exception.
 *
 * Exceptions of this class are caused by other errors generated
 * by other nodes for portal platform.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PlatformException extends PortalException {
    public PlatformException(PortalProtocolException ex) {
        super(PortalErrorTranslator.translate(ex), ex.getMessage());
    }
}
