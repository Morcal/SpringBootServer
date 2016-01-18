package cn.com.xinli.portal.core;

/**
 * Certificate Not Found Exception.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public class CertificateNotFoundException extends PortalException {
    public CertificateNotFoundException(String message) {
        super(PortalError.of("invalid_client"), message);
    }
}
