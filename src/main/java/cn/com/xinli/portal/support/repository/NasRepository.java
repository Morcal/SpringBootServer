package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.nas.Nas;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Nas/bras device repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface NasRepository extends CrudRepository<Nas, Long>, Searchable<Nas> {
    /**
     * Delete nas.
     * @param name nas name.
     */
    @Modifying
    @Query("delete from Nas n where n.name = :name")
    void delete(@Param("name") String name);
}
