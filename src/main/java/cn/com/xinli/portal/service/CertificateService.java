package cn.com.xinli.portal.service;

import cn.com.xinli.portal.core.CertificateNotFoundException;
import cn.com.xinli.portal.core.Certificate;

/**
 * Certificate service.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
public interface CertificateService {
    /**
     * Check if given app id already certified.
     * @return true if app id already certified.
     * @param clientId client id.
     */
    boolean isCertified(String clientId);

    /**
     * Disable an existed certificate.
     * @param id certificate id.
     */
    void disableCertificate(long id);

    /**
     * Load certificate by client id, aka app id.
     * @param clientId client id.
     * @return certificated.
     */
    Certificate loadCertificate(String clientId) throws CertificateNotFoundException;
}
