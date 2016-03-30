package cn.com.xinli.portal.core.configuration;

/**
 * Server configuration service.
 * @author zhoupeng, created on 2016/3/31.
 */
public interface ServerConfigurationService {

    boolean isServerConfigured();

    void save(Configuration config);

    void save(ConfigurationEntry entry);

    Iterable<ConfigurationEntry> all();
}
