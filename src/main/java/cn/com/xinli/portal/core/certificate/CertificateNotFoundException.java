package cn.com.xinli.portal.core.certificate;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;

/**
 * Certificate Not Found Exception.
 *
 * <p>This exception will throw when try to retrieve certificate by identity
 * provided in request credentials.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class CertificateNotFoundException extends PortalException {
    public CertificateNotFoundException(String message) {
        super(PortalError.INVALID_CERTIFICATE, message);
    }
}
