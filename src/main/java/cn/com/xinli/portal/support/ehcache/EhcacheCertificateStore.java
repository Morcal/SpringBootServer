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
import java.util.Objects;

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
        for (Certificate certificate : certificateRepository.findAll()) {
            put(certificate);
        }

        logger.info("EhCache certificate sync with database done.");
    }

    @Override
    public Certificate get(String appId) throws CertificateNotFoundException {
        Element element = certificateCache.get(appId);
        if (element == null) {
            throw new CertificateNotFoundException(appId);
        }

        return (Certificate) element.getObjectValue();
    }

    @Override
    public void put(Certificate certificate) {
        Objects.requireNonNull(certificate);
        /* save to database, id will be generated if missing. */
        certificateRepository.save(certificate);
        Element element = new Element(certificate.getAppId(), certificate);
        certificateCache.put(element);
        if (logger.isTraceEnabled()) {
            logger.trace("certificate saved in cache, {}", certificate);
        }
    }

    @Override
    public boolean exists(String appId) {
        return certificateCache.get(appId) != null;
    }

    @Override
    public boolean delete(String appId) throws CertificateNotFoundException {
        if (logger.isTraceEnabled()) {
            logger.trace("deleting certificate {{}}", appId);
        }
        certificateRepository.delete(appId);
        return certificateCache.remove(appId);
    }
}
