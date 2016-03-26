package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateStore;
import cn.com.xinli.portal.support.repository.CertificateRepository;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Certificate store based on <a href="http://ehcache.org">EhCache</a>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Component
@Profile("standalone")
public class EhcacheCertificateStore implements CertificateStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhcacheCertificateStore.class);

    @Qualifier("certificateRepository")
    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private Ehcache certificateCache;

    @PostConstruct
    public void init() {
        certificateRepository.findAll().forEach(this::put);

        logger.info("EhCache certificate sync with database done.");
    }

    @Override
    public Certificate find(String appId) throws CertificateNotFoundException {
        for (Object key : certificateCache.getKeys()) {
            Element element = certificateCache.get(key);
            Certificate certificate = (Certificate) element.getObjectValue();
            if (certificate.getAppId().equals(appId))
                return certificate;
        }

        throw new CertificateNotFoundException(appId);
//        Element element = certificateCache.get(appId);
//        if (element == null) {
//            throw new CertificateNotFoundException(appId);
//        }
//
//        return (Certificate) element.getObjectValue();
    }

    @Override
    public Stream<Certificate> all() {
        List<Certificate> certificates = new ArrayList<>();
        for (Certificate certificate : certificateRepository.findAll()) {
            certificates.add(certificate);
        }

        return certificates.stream();
    }

    @Override
    public Stream<Certificate> search(String query) {
        return certificateRepository.search(query);
    }

    @Override
    public void put(Certificate certificate) {
        Objects.requireNonNull(certificate, Certificate.EMPTY_CERTIFICATE);
        /* save to database, id will be generated if missing. */
        certificateRepository.save(certificate);
        Element element = new Element(certificate.getId(), certificate);
        certificateCache.put(element);
        if (logger.isTraceEnabled()) {
            logger.trace("certificate saved in cache, {}", certificate);
        }
    }

    @Override
    public boolean exists(Long id) {
        return certificateCache.get(id) != null;
    }

    @Override
    public Certificate get(Long id) throws CertificateNotFoundException {
        return (Certificate) certificateCache.get(id).getObjectValue();
    }

    @Override
    public boolean delete(Long id) throws CertificateNotFoundException {
        if (logger.isTraceEnabled()) {
            logger.trace("deleting certificate {{}}", id);
        }
        certificateRepository.delete(id);
        return certificateCache.remove(id);
    }
}
