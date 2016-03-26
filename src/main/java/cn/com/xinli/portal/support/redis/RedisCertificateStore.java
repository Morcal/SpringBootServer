package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateStore;
import cn.com.xinli.portal.support.configuration.ClusterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Component
@Profile("cluster")
public class RedisCertificateStore implements CertificateStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RedisCertificateStore.class);

    @Autowired
    @Qualifier("redisCertificateTemplate")
    private RedisTemplate<String, Certificate> certificateRedisTemplate;

    @Autowired
    @Qualifier("redisQueryTemplate")
    private RedisTemplate<String, Long> redisQueryTemplate;

    /**
     * Generate a REDIS value key as "certificate:client" so
     * that it can be searched.
     * @param clientId client id/app id.
     * @return REDIS key.
     */
    String keyFor(String clientId) {
        return "certificate:app:" + clientId + "";
    }

    String keyFor(Long id) {
        return "certificate:id:" + id;
    }

    /**
     * Generate a REDIS value key as "certificate:client:os:version" so
     * that it can be searched.
     *
     * @param clientId client id/app id.
     * @param os operation system name.
     * @param version client/app version.
     * @return REDIS key.
     */
    private String keyFor(String clientId, String os, String version) {
        return "certificate:" + clientId + ":" + os + ":" + version;
    }

    String keyFor(Certificate certificate) {
        return keyFor(certificate.getAppId());
    }

    @Override
    public Certificate get(Long id) throws CertificateNotFoundException {
        Certificate certificate = certificateRedisTemplate.opsForValue().get(keyFor(id));
        if (certificate == null) {
            throw new CertificateNotFoundException(id);
        }
        return certificate;
    }

    @Override
    public void put(Certificate certificate) {
        Objects.requireNonNull(certificate, Certificate.EMPTY_CERTIFICATE);
        certificateRedisTemplate.opsForValue().set(keyFor(certificate), certificate);
        /* certificate:client for searching. */
        redisQueryTemplate.opsForValue().set(keyFor(certificate.getAppId()), certificate.getId());
        /* certificate:client:os:version for searching. */
        redisQueryTemplate.opsForValue().set(
                keyFor(certificate.getAppId(), certificate.getOs(), certificate.getVersion()), certificate.getId());
        if (logger.isTraceEnabled()) {
            logger.trace("certificate saved {}", certificate);
        }

        CertificateMessage message = new CertificateMessage();
        message.setType(CertificateMessage.Type.ADDED);
        message.setCertificate(certificate);
        certificateRedisTemplate.convertAndSend(ClusterConfiguration.CERTIFICATE_CHANNEL, message);

        if (logger.isTraceEnabled()) {
            logger.trace("certificate add notified {}", certificate);
        }
    }

    @Override
    public boolean exists(Long id) {
        return certificateRedisTemplate.opsForValue().get(keyFor(id)) != null;
    }

    @Override
    public boolean delete(Long id) throws CertificateNotFoundException {
        Certificate certificate = get(id);
        certificateRedisTemplate.delete(keyFor(certificate));
        redisQueryTemplate.delete(keyFor(id));
        redisQueryTemplate.delete(keyFor(certificate.getAppId(), certificate.getOs(), certificate.getVersion()));
        boolean removed = !exists(certificate.getId());
        if (logger.isTraceEnabled()) {
            logger.trace("certificate deleted {}", removed);
        }

        if (removed) {
            CertificateMessage message = new CertificateMessage();
            message.setType(CertificateMessage.Type.REMOVED);
            message.setCertificate(certificate);
            certificateRedisTemplate.convertAndSend(ClusterConfiguration.CERTIFICATE_CHANNEL, message);

            if (logger.isTraceEnabled()) {
                logger.trace("certificate delete notified {}", certificate);
            }
        }
        return removed;
    }

    @Override
    public Certificate find(String app) throws CertificateNotFoundException {
        Long id = redisQueryTemplate.opsForValue().get(keyFor(app));
        if (id == null) {
            throw new CertificateNotFoundException("certificate with app: " + app + " not found.");
        }

        return certificateRedisTemplate.opsForValue().get(id);
    }

    @Override
    public Stream<Certificate> all() {
        Set<String> keys = certificateRedisTemplate.keys("certificate:id:");
        Set<Certificate> certificates = keys.stream()
                .map(key -> certificateRedisTemplate.opsForValue().get(key))
                .collect(Collectors.toSet());
        return certificates.stream();
    }

    @Override
    public Stream<Certificate> search(String query) {
        Set<String> keys = certificateRedisTemplate.keys("certificate:app:" + query);
        Set<Certificate> certificates = keys.stream()
                .map(key -> certificateRedisTemplate.opsForValue().get(key))
                .collect(Collectors.toSet());
        return certificates.stream();
    }
}
