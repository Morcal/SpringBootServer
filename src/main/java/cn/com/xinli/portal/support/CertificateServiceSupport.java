package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.certificate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Certificate Service Support.
 *
 * <p>Unsolved issues:
 * FIXME {@link #loadCertificate(String)} will be insufficient when certificate evolved.
 * FIXME {@link #isCertified(String)} will be insufficient when certificate evolved.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class CertificateServiceSupport implements CertificateService, CertificateManager {

    @Autowired
    private CertificateStore certificateStore;

    @Override
    public Certificate create(String appId, String vendor, String os, String version, String sharedSecret) {
        Certificate certificate = new Certificate();
        certificate.setAppId(appId);
        certificate.setVersion(version);
        certificate.setVendor(vendor);
        certificate.setOs(os);
        certificate.setSharedSecret(sharedSecret);
        certificateStore.put(certificate);
        return certificate;
    }

    @Override
    public void disableCertificate(String id) throws CertificateNotFoundException {
        Certificate certificate = certificateStore.get(id);
        certificate.setDisabled(true);
        certificateStore.put(certificate);
    }

    @Override
    public void delete(Certificate certificate) throws CertificateNotFoundException {
        Objects.requireNonNull(certificate, Certificate.EMPTY_CERTIFICATE);
        certificateStore.delete(certificate.getAppId());
    }

    @Override
    public boolean isCertified(String clientId) {
        try {
            return loadCertificate(clientId) != null;
        } catch (CertificateNotFoundException e) {
            return false;
        }
    }

    @Override
    public Certificate loadCertificate(String clientId) throws CertificateNotFoundException {
        return certificateStore.get(clientId);
    }
}
