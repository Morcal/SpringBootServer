package cn.com.xinli.portal.persist;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Nas/bras device respositoy.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
@Transactional
public interface NasRepository extends PagingAndSortingRepository<NasEntity, Long> {
    /**
     * Get all nas configurations.
     * @return all nas configurations.
     */
    @Query("from NasEntity")
    List<NasEntity> all();
}
