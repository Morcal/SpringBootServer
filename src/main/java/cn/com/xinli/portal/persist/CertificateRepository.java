package cn.com.xinli.portal.persist;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface CertificateRepository extends CrudRepository<CertificateEntity, Long> {
    /**
     * Find app authentication entities.
     * @param appId app id.
     * @return app authentication list if found or null.
     */
    List<CertificateEntity> find(String appId);
}
