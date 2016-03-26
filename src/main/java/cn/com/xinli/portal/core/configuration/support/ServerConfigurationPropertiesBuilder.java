package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Server configuration properties builder.
 *
 * <p>This class provide abilities to load server configuration from a properties file.
 * @author zhoupeng, created on 2016/3/25.
 */
public class ServerConfigurationPropertiesBuilder {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ServerConfigurationPropertiesBuilder.class);

    //private static final String PROPERTIES_RESOURCE = "pws.properties";

    /** Builder properties content. */
    private String content;

    /** Properties file name. */
    private String filename;

    private Properties properties;

    public ServerConfigurationPropertiesBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public ServerConfigurationPropertiesBuilder setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public ServerConfigurationPropertiesBuilder setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Load default configurations.
     * @return properties.
     * @throws ServerException
     */
    protected Properties loadDefaults() throws ServerException {
        InputStream in = null;
        try {
            in = getClass().getClassLoader().getResourceAsStream("defaults.properties");
            Properties defaults = new Properties();
            defaults.load(in);
            in.close();
            return defaults;
        } catch (IOException e) {
            throw new ServerException(PortalError.MISSING_PWS_CONFIGURATION,
                    "read configuration failed.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Validate specified {@code properties}.
     * @param properties properties.
     * @throws ServerException
     */
    Configuration createConfigurationFromProperties(Properties properties)
            throws ServerException {
        Configuration configuration = new Configuration();
        configuration.load(properties);
        return configuration;
    }

    /**
     * Build a properties based server configuration.
     * @return configuration.
     * @throws ServerException
     */
    public Configuration build() throws ServerException {
        if (this.properties == null && content == null && filename == null)
            throw new IllegalStateException("Properties or content not been set.");

        /* Load defaults. */
        Properties properties = new Properties(loadDefaults());
        final InputStream in;

        if (this.properties != null) {
            for (Object key : this.properties.keySet()) {
                properties.put(key, this.properties.get(key));
            }
            return createConfigurationFromProperties(properties);
        } else if (filename != null) {
            in = getClass().getClassLoader().getResourceAsStream(filename);
        } else if (content != null) {
            in = new ByteArrayInputStream(content.getBytes());
        } else {
            throw new ServerException(PortalError.MISSING_PWS_CONFIGURATION, "unsupported configuration");
        }

        try {
            properties.load(in);
            return createConfigurationFromProperties(properties);
        } catch (IOException e) {
            logger.error("Failed to load properties from string", e);
            throw new ServerException(PortalError.MISSING_PWS_CONFIGURATION,
                    "read configuration failed.", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
