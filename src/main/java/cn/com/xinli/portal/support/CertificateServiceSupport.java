package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.certificate.*;
import cn.com.xinli.portal.web.util.SecureRandomStringGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Stream;

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

    @Autowired
    private SecureRandomStringGenerator secureRandomStringGenerator;

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
    public void save(Certificate certificate) throws CertificateNotFoundException {
        certificateStore.put(certificate);
    }

    @Override
    public void delete(Certificate certificate) throws CertificateNotFoundException {
        Objects.requireNonNull(certificate, Certificate.EMPTY_CERTIFICATE);
        certificateStore.delete(certificate.getId());
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
        return certificateStore.find(clientId);
    }

    @Override
    public Certificate get(Long id) throws CertificateNotFoundException {
        return certificateStore.get(id);
    }

    @Override
    public Stream<Certificate> all() {
        return certificateStore.all();
    }

    @Override
    public Stream<Certificate> search(String query) throws RemoteException {
        if (StringUtils.isEmpty(query)) {
            return all();
        } else {
            return certificateStore.search(query);
        }
    }

    @Override
    public Certificate create(Certificate certificate) {
        String sharedSecret = StringUtils.isEmpty(certificate.getSharedSecret()) ?
                secureRandomStringGenerator.generateUniqueRandomString(16) : certificate.getSharedSecret();
        return create(certificate.getAppId(), certificate.getVendor(), certificate.getOs(),
                certificate.getVersion(), sharedSecret);
    }
}
