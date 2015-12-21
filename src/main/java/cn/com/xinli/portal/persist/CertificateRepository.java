package cn.com.xinli.portal.persist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
@Transactional
public interface CertificateRepository extends CrudRepository<CertificateEntity, Long> {
    /**
     * Find app authentication entities.
     * @param appId app id.
     * @return app authentication list if found or null.
     */
    @Query("select c from CertificateEntity c where c.appId = :appId")
    List<CertificateEntity> find(@Param("appId") String appId);
}
