package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.support.repository.ServerConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * Server configuration service.
 * @author zhoupeng, created on 2016/3/31.
 */
@Service
@Transactional(rollbackFor = DataAccessException.class)
public class ServerConfigurationServiceSupport implements ServerConfigurationService {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ServerConfigurationServiceSupport.class);

    @Qualifier("serverConfigurationRepository")
    @Autowired
    private ServerConfigurationRepository repository;

    /** Configuration. */
    private final Configuration configuration = new Configuration();

    /** Server configuration. */
    private final PropertiesServerConfiguration serverConfiguration =
            new PropertiesServerConfiguration();

    /**
     * If server already configured.
     * @return true if already configured.
     */
    private boolean isServerConfigured() {
        return repository.count() > 0;
    }

    /**
     * Load server configuration from database.
     *
     * <p>If server configuration does not exist in the database,
     * a <em>Default</em> configuration will be applied.
     *
     * <p>If server configuration has been modified, this method should
     * be called to apply those modifications.
     */
    @PostConstruct
    public void init() throws ServerException, ServerConfigurationNotExistsException {
        if (!isServerConfigured()) {
            logger.warn("server configuration not exists, using defaults.");
            /* Server configuration never been saved yet. */
            Properties properties = Configuration.loadDefaults();

            Collection<ConfigurationEntry> entries = new ArrayList<>();

            for (String key : Configuration.keys()) {
                String v = properties.getProperty(key);
                ConfigurationEntry entry =
                        ConfigurationEntry.of(key, Configuration.getMetadata(key).get().getValueType(), v);
                entries.add(entry);
            }

            repository.save(entries);
            configuration.setEntries(entries);
        } else {
            configuration.setEntries(repository.findAll());
        }

        serverConfiguration.load(configuration);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void save(ConfigurationEntry entry) {
        repository.save(entry);
    }

    @Override
    public ServerConfiguration getServerConfiguration() {
        return serverConfiguration;
    }

    @Override
    public void updateConfigurationEntry(String key, String value)
            throws ServerConfigurationNotExistsException, ServerException {

        ConfigurationEntry entry = configuration.getEntry(key);

        if (logger.isTraceEnabled()) {
            logger.trace("updating new value {} to {}", value, entry);
        }

        entry.updateValue(value);

        save(entry);

        serverConfiguration.load(configuration);

        if (logger.isTraceEnabled()) {
            logger.trace("server configuration updated to {}", serverConfiguration);
        }
    }
}
