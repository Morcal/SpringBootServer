package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.certificate.Certificate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

/**
 * Certificate Repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface CertificateRepository extends CrudRepository<Certificate, Long>, Searchable<Certificate> {
    /**
     * Find app authentication entities.
     * @param appId app id.
     * @return app authentication list if found or null.
     */
    @Query("select c from Certificate c where c.appId = :appId")
    Stream<Certificate> find(@Param("appId") String appId);
}
