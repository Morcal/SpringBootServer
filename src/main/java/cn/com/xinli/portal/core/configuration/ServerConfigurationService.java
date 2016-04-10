package cn.com.xinli.portal.core.configuration;

import cn.com.xinli.portal.core.ServerException;

/**
 * Server configuration service.
 * @author zhoupeng, created on 2016/3/31.
 */
public interface ServerConfigurationService {
    Configuration getConfiguration() throws ServerException;

    void save(ConfigurationEntry entry);

    ServerConfiguration getServerConfiguration();

    void updateConfigurationEntry(String key, String value) throws ServerException;
}
