package cn.com.xinli.portal.support.repository;

import cn.com.xinli.portal.core.configuration.ConfigurationEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Server Configuration Repository.
 * @author zhoupeng, created on 2016/3/25.
 */
@Repository
public interface ServerConfigurationRepository extends CrudRepository<ConfigurationEntry, Long> {
}
