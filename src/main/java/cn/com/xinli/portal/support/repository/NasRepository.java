package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.nas.Nas;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Nas/bras device respositoy.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Repository
public interface NasRepository extends PagingAndSortingRepository<Nas, Long> {
}
