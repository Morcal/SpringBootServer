package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.configuration.Configuration;
import cn.com.xinli.portal.core.configuration.ConfigurationEntry;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.support.repository.ServerConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Server configuration service.
 * @author zhoupeng, created on 2016/3/31.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class ServerConfigurationServiceSupport implements ServerConfigurationService {
    @Qualifier("serverConfigurationRepository")
    @Autowired
    private ServerConfigurationRepository repository;

    @Override
    public boolean isServerConfigured() {
        return repository.count() > 0;
    }

    @Override
    public void save(Configuration config) {
        repository.save(config.entries());
    }

    @Override
    public void save(ConfigurationEntry entry) {
        repository.save(entry);
    }

    @Override
    public Iterable<ConfigurationEntry> all() {
        return repository.findAll();
    }
}
