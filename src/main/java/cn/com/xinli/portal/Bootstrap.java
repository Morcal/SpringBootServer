package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.ServerConfigurationPropertiesBuilder;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.support.HuaweiPortalSessionProvider;
import cn.com.xinli.portal.support.InterProcessNpsSessionProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
@org.springframework.context.annotation.Configuration
@Order(Stage.CONFIGURE)
public class Bootstrap {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Bean
    public Configuration configuration() throws ServerException {
        boolean firstRun = serverConfigurationService.isServerConfigured();
        final Properties properties;
        if (firstRun) {
            /* Server configuration never been saved yet. */
            properties = new ServerConfigurationPropertiesBuilder().loadDefaults();
        } else {
            properties = new Properties();

            for (ConfigurationEntry e : serverConfigurationService.all()) {
                if (!StringUtils.isEmpty(e.getValueText())) {
                    properties.setProperty(e.getKey(), e.getValueText());
                }
            }

            if (properties.keySet().size() == 0) {
                logger.warn("server configuration not exists, using defaults.");
            }
        }

        return new ServerConfigurationPropertiesBuilder()
                        .setProperties(properties)
                        .build();
    }

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
     * @throws ServerConfigurationNotExistsException
     */
    @Bean
    @Autowired
    public ServerConfiguration serverConfiguration(Configuration config)
            throws ServerException, ServerConfigurationNotExistsException {
        PropertiesServerConfiguration configuration = new PropertiesServerConfiguration();
        configuration.load(config);

        if (!serverConfigurationService.isServerConfigured()) {
            serverConfigurationService.save(config);
        }

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
