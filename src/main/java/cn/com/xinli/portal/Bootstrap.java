package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.ServerConfigurationPropertiesBuilder;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.support.HuaweiPortalSessionProvider;
import cn.com.xinli.portal.support.InterProcessNpsSessionProvider;
import cn.com.xinli.portal.support.repository.ServerConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Portal web server bootstrap.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
@Configuration
@Order(Stage.CONFIGURE)
public class Bootstrap {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Qualifier("serverConfigurationRepository")
    @Autowired
    private ServerConfigurationRepository repository;

    /**
     * Load server configuration from database.
     *
     * <p>If server configuration does not exist in the database,
     * a <em>Default</em> configuration will be applied.
     *
     * <p>If server configuration has been modified, this method should
     * be called to apply those modifications. TODO verify it!
     * @return server configuration.
     * @throws ServerException
     */
    @Bean
    public ServerConfiguration serverConfiguration() throws ServerException {
        PropertiesServerConfiguration configuration = new PropertiesServerConfiguration();
        Properties properties = new Properties();
        repository.findAll().forEach(entry ->
                properties.setProperty(entry.getKey(), entry.getValueText()));

        if (properties.keySet().size() == 0) {
            logger.warn("server configuration not exists, using defaults.");
        }

        PropertiesServerConfiguration.Configuration config =
                new ServerConfigurationPropertiesBuilder()
                .setProperties(properties)
                .build();

        configuration.load(config);
        return configuration;
    }

    /**
     * Setup system supported session providers.
     * @return list of session providers.
     */
    @Bean
    public List<SessionProvider> sessionProviders() {
        List<SessionProvider> providers = new ArrayList<>();
        providers.add(new HuaweiPortalSessionProvider());
        providers.add(new InterProcessNpsSessionProvider());
        logger.info("Session providers loaded, {}", providers.size());
        return providers;
    }
}
