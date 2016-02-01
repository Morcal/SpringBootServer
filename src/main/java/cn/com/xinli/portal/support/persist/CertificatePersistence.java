package cn.com.xinli.portal.support.persist;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.support.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Certificate persistence.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Component
public class CertificatePersistence {
    @Qualifier("certificateRepository")
    @Autowired
    private CertificateRepository certificateRepository;

    public void all(Consumer<Certificate> consumer) {
        certificateRepository.findAll().forEach(consumer);
    }

    public Certificate save(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public void delete(String appId) {
        certificateRepository.delete(appId);
    }
}
