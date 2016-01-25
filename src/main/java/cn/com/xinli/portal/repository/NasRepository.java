package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.core.Nas;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Nas/bras device respositoy.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public interface NasRepository extends PagingAndSortingRepository<Nas, Long> {
}
