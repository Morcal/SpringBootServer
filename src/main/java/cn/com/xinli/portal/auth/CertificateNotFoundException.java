package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.PortalException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class CertificateNotFoundException extends PortalException {
    public CertificateNotFoundException(String message) {
        super(message);
    }
}
