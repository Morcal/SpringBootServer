package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.radius.Radius;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * RADIUS server repository.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Repository
public interface RadiusRepository extends CrudRepository<Radius, Long> {
}
