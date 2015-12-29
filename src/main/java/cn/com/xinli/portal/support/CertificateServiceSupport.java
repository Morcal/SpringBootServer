package cn.com.xinli.portal.support;

import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.auth.CertificateManager;
import cn.com.xinli.portal.auth.CertificateNotFoundException;
import cn.com.xinli.portal.auth.CertificateService;
import cn.com.xinli.portal.persist.CertificateEntity;
import cn.com.xinli.portal.persist.CertificateRepository;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Certificate Service Support.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/21.
 */
@Service
public class CertificateServiceSupport implements CertificateService, CertificateManager {
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private Ehcache certificateCache;

    @Override
    public Certificate create(String appId, String vendor, String os, String version) {
        CertificateEntity certificate = new CertificateEntity();
        certificate.setAppId(appId);
        certificate.setVersion(version);
        certificate.setVendor(vendor);
        certificate.setOs(os);
        certificateRepository.save(certificate);

        certificateCache.put(new Element(appId, certificate));

        return certificate;
    }

    @Override
    public void disableCertificate(long id) {
        CertificateEntity certificate = certificateRepository.findOne(id);
        certificate.setDisabled(true);
        certificateRepository.save(certificate);
        certificateCache.put(new Element(certificate.getAppId(), certificate));
    }

    @Override
    public void delete(Certificate certificate) {
        certificateCache.remove(certificate.getAppId());
        certificateRepository.delete((CertificateEntity) certificate);
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
            Optional<CertificateEntity> opt = certificateRepository.find(clientId).stream().findFirst();

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
