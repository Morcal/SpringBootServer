package cn.com.xinli.portal.service;

import cn.com.xinli.portal.core.Certificate;
import cn.com.xinli.portal.core.CertificateManager;
import cn.com.xinli.portal.core.CertificateNotFoundException;
import cn.com.xinli.portal.repository.CertificateRepository;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Certificate Service Support.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class CertificateServiceSupport implements CertificateService, CertificateManager {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private Ehcache certificateCache;

    @Override
    public Certificate create(String appId, String vendor, String os, String version, String sharedSecret) {
        Certificate certificate = new Certificate();
        certificate.setAppId(appId);
        certificate.setVersion(version);
        certificate.setVendor(vendor);
        certificate.setOs(os);
        certificate.setSharedSecret(sharedSecret);
        certificateRepository.save(certificate);

        certificateCache.put(new Element(appId, certificate));

        return certificate;
    }

    @Override
    public void disableCertificate(long id) {
        Certificate certificate = certificateRepository.findOne(id);
        certificate.setDisabled(true);
        certificateRepository.save(certificate);
        certificateCache.put(new Element(certificate.getAppId(), certificate));
    }

    @Override
    public void delete(Certificate certificate) {
        certificateCache.remove(certificate.getAppId());
        certificateRepository.delete(certificate);
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
        Element element = certificateCache.get(clientId);
        if (element == null) {
            Optional<Certificate> opt = certificateRepository.find(clientId).stream().findFirst();

            if (opt.isPresent()) {
                certificateCache.put(new Element(clientId, opt.get()));
            }

            opt.ifPresent(cert -> certificateCache.put(new Element(clientId, cert)));
            opt.orElseThrow(() -> new CertificateNotFoundException(clientId));

            return opt.get();
        } else {
            return (Certificate) element.getObjectValue();
        }
    }
}
