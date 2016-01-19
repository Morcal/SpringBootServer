package cn.com.xinli.portal.core;

import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.support.PortalErrorTranslator;

/**
 * Portal platform (including NAS/BRAS, AAA platform) exception.
 * <p>
 * Exceptions of this class are caused by other errors generated
 * by other nodes for portal platform.
 * <p>
 * Portal protocol exceptions are thrown during server communicating with
 * NAS/BRAS. Errors may occurs in the underlying communicating layer or
 * occurs in the business logic layer like portal related authentication.
 * <p>
 * When a {@link PortalProtocolException} been thrown, server should translate
 * underlying exception to {@link PortalError} and then send error response
 * to remote client.
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PlatformException extends PortalException {
    public PlatformException(PortalProtocolException ex) {
        super(PortalErrorTranslator.translate(ex), ex.getMessage());
    }
}
