package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides a java properties configuration file based
 * portal server configuration implementation.
 *
 * <p>{@link cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration}
 * load a default configurations and then load customized properties
 * named "pws.properties" in classpath to override default settings if presents.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class PropertiesServerConfiguration {
    /** Server private key. */
    public static final String SERVER_PRIVATE_KEY = "private-key";
    /** Main page redirect url. */
    public static final String MAIN_PAGE_REDIRECT_URL = "main-page.redirect.url";
    /** Allow nat. */
    public static final String NAT_ALLOWED = "nat.allowed";
    /** Rest configurations. */
    public static final String REST_HOST = "rest.host";
    public static final String REST_SERVER = "rest.server";
    public static final String REST_SCHEME = "rest.scheme";
    public static final String REST_HEADER = "rest.header";
    public static final String REST_META = "rest.meta";
    public static final String REST_CHALLENGE_TTL = "rest.challenge.ttl";
    /** Cluster configurations. */
    public static final String CLUSTER_ENABLED = "cluster.enabled";
    public static final String CLUSTER_REDIS_MASTER = "cluster.redis.master";
    public static final String CLUSTER_REDIS_SENTINELS = "cluster.redis.sentinels";
    /** Activity configurations. */
    public static final String ACTIVITY_MOST_RECENT = "activity.most-recent";
    public static final String ACTIVITY_LOGGING_MIN_SEVERITY = "activity.logging.severity";
    /** Session configurations. */
    public static final String SESSION_TTL_ENABLED = "session.ttl.enabled";
    public static final String SESSION_TTL_VALUE = "session.ttl.value";
    public static final String SESSION_HEARTBEAT_ENABLED = "session.heartbeat.enabled";
    public static final String SESSION_HEARTBEAT_INTERVAL = "session.heartbeat.interval";
    public static final String SESSION_UPDATE_MIN_INTERVAL = "session.update.min-interval";
    /** Rate-limiting configurations. */
    public static final String RATE_LIMITING_ENABLED = "rate-limiting.enabled";
    public static final String RATE_LIMITING_RATE = "rate-limiting.rate";
    public static final String RATE_LIMITING_TTL = "rate-limiting.ttl";
    /** Portal server configurations. */
    public static final String PORTAL_SERVER_HOST = "portal-server.host";
    public static final String PORTAL_SERVER_NAME = "portal-server.name";
    public static final String PORTAL_SERVER_PROTOCOL_VERSION = "portal-server.protocol.version";
    public static final String PORTAL_SERVER_LISTEN_PORT = "portal-server.listen.port";
    public static final String PORTAL_SERVER_CORE_THREADS = "portal-server.core-threads";
    public static final String PORTAL_SERVER_SHARED_SECRET = "portal-server.shared-secret";
    /**
     * Configuration options for Developing.
     */
    public static final String MOCK_NAS_HOST = "mock-nas.host";
    public static final String MOCK_NAS_ENABLED = "mock-nas.enabled";
    public static final String MOCK_NAS_NAME = "mock-nas.name";
    public static final String MOCK_NAS_LISTEN_PORT = "mock-nas.listen.port";
    public static final String MOCK_NAS_SHARED_SECRET = "mock-nas.shared-secret";

    @Autowired
    private ResourceLoader resourceLoader;

    private PortalServerConfiguration mockNas;

    public PortalServerConfiguration getMockNas() {
        return mockNas;
    }

    public ServerConfiguration loadFromProperties(String resource) throws ServerException {
        ServerConfiguration configuration = new ServerConfiguration();

        Configuration config = new Configuration();
        config.load(resource);

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config.valueOf(ACTIVITY_MOST_RECENT));
        activity.setSeverity(config.valueOf(ACTIVITY_LOGGING_MIN_SEVERITY));
        configuration.setActivityConfiguration(activity);

        /* Create session configuration. */
        SessionConfiguration session = new SessionConfiguration();
        session.setEnableHeartbeat(config.valueOf(SESSION_HEARTBEAT_ENABLED));
        if (session.isEnableHeartbeat()) {
            session.setHeartbeatInterval(config.valueOf(SESSION_HEARTBEAT_INTERVAL));
        }
        session.setEnableTtl(config.valueOf(SESSION_TTL_ENABLED));
        if (session.isEnableTtl()) {
            session.setTtl(config.valueOf(SESSION_TTL_VALUE));
        }
        configuration.setSessionConfiguration(session);

        /* Create portal server configuration(s). */
        PortalServerConfiguration internal = new PortalServerConfiguration();
        internal.setHost(config.valueOf(PORTAL_SERVER_HOST));
        internal.setName(config.valueOf(PORTAL_SERVER_NAME));
        internal.setPort(config.valueOf(PORTAL_SERVER_LISTEN_PORT));
        internal.setVersion(config.valueOf(PORTAL_SERVER_PROTOCOL_VERSION));
        internal.setSharedSecret(config.valueOf(PORTAL_SERVER_SHARED_SECRET));
        configuration.setPortalServerConfiguration(internal);

        /* Create mock huawei NAS (portal server) configuration. */
        configuration.setEnableMockedHuaweiNas(config.valueOf(MOCK_NAS_ENABLED));
        if (configuration.isEnableMockedHuaweiNas()) {
            mockNas = new PortalServerConfiguration();
            mockNas.setHost(config.valueOf(MOCK_NAS_HOST));
            mockNas.setName(config.valueOf(MOCK_NAS_NAME));
            mockNas.setPort(config.valueOf(MOCK_NAS_LISTEN_PORT));
            mockNas.setSharedSecret(config.valueOf(MOCK_NAS_SHARED_SECRET));
        }

        /* Create rate limiting configuration. */
        configuration.setEnableRateLimiting(config.valueOf(RATE_LIMITING_ENABLED));
        if (configuration.isEnableRateLimiting()) {
            RateLimitingConfiguration rate = new RateLimitingConfiguration();
            rate.setRate(config.valueOf(RATE_LIMITING_RATE));
            rate.setTtl(config.valueOf(RATE_LIMITING_TTL));
            configuration.setRateLimitingConfiguration(rate);
        }

        /* Create rest configuration. */
        RestConfiguration rest = new RestConfiguration();
        rest.setChallengeTtl(config.valueOf(REST_CHALLENGE_TTL));
        rest.setHeader(config.valueOf(REST_HEADER));
        rest.setHost(config.valueOf(REST_HOST));
        rest.setServer(config.valueOf(REST_SERVER));
        rest.setMeta(config.valueOf(REST_META));
        rest.setScheme(config.valueOf(REST_SCHEME));
        configuration.setRestConfiguration(rest);

        /* Create cluster configuration. */
        configuration.setEnableCluster(config.valueOf(CLUSTER_ENABLED));
        if (configuration.isEnableCluster()) {
            ClusterConfiguration cluster = new ClusterConfiguration();
            cluster.setRedisMaster(config.valueOf(CLUSTER_REDIS_MASTER));
            String sentinels = config.valueOf(CLUSTER_REDIS_SENTINELS);
            cluster.setRedisSentinels(sentinels.split(","));
            configuration.setClusterConfiguration(cluster);
        }

        return configuration;
    }

    class Configuration {
        Entry[] entries = {
                /* Server private key. */
                Entry.of(SERVER_PRIVATE_KEY, ValueType.STRING),
                /* Main page redirect url. */
                Entry.of(MAIN_PAGE_REDIRECT_URL, ValueType.STRING),
                /* Allow nat. */
                Entry.of(NAT_ALLOWED, ValueType.BOOLEAN),
                /* Rest configurations. */
                Entry.of(REST_HOST, ValueType.STRING),
                Entry.of(REST_SERVER, ValueType.STRING),
                Entry.of(REST_SCHEME, ValueType.STRING),
                Entry.of(REST_HEADER, ValueType.STRING),
                Entry.of(REST_META, ValueType.STRING),
                Entry.of(REST_CHALLENGE_TTL, ValueType.INTEGER),
                /* Cluster configurations. */
                Entry.of(CLUSTER_ENABLED, ValueType.BOOLEAN),
                Entry.of(CLUSTER_REDIS_MASTER, ValueType.STRING),
                Entry.of(CLUSTER_REDIS_SENTINELS, ValueType.STRING),
                /* Activity configurations. */
                Entry.of(ACTIVITY_MOST_RECENT, ValueType.INTEGER),
                Entry.of(ACTIVITY_LOGGING_MIN_SEVERITY, ValueType.SEVERITY),
                /* Session configurations. */
                Entry.of(SESSION_TTL_ENABLED, ValueType.BOOLEAN),
                Entry.of(SESSION_TTL_VALUE, ValueType.INTEGER),
                Entry.of(SESSION_HEARTBEAT_ENABLED, ValueType.BOOLEAN),
                Entry.of(SESSION_HEARTBEAT_INTERVAL, ValueType.INTEGER),
                Entry.of(SESSION_UPDATE_MIN_INTERVAL, ValueType.INTEGER),
                /* Rate-limiting configurations. */
                Entry.of(RATE_LIMITING_ENABLED, ValueType.BOOLEAN),
                Entry.of(RATE_LIMITING_RATE, ValueType.INTEGER),
                Entry.of(RATE_LIMITING_TTL, ValueType.INTEGER),
                /* Portal server configurations. */
                Entry.of(PORTAL_SERVER_NAME, ValueType.STRING),
                Entry.of(PORTAL_SERVER_HOST, ValueType.STRING),
                Entry.of(PORTAL_SERVER_PROTOCOL_VERSION, ValueType.STRING),
                Entry.of(PORTAL_SERVER_LISTEN_PORT, ValueType.INTEGER),
                Entry.of(PORTAL_SERVER_CORE_THREADS, ValueType.INTEGER),
                Entry.of(PORTAL_SERVER_SHARED_SECRET, ValueType.STRING),
                /* Mock HUAWEI NAS configurations. */
                Entry.of(MOCK_NAS_HOST, ValueType.STRING),
                Entry.of(MOCK_NAS_ENABLED, ValueType.BOOLEAN),
                Entry.of(MOCK_NAS_NAME, ValueType.STRING),
                Entry.of(MOCK_NAS_LISTEN_PORT, ValueType.INTEGER),
                Entry.of(MOCK_NAS_SHARED_SECRET, ValueType.STRING),
        };

        <T>T valueOf(String entry) {
            return getEntry(entry).getValue();
        }

        public Entry getEntry(String name) {
            for (Entry entry : entries) {
                if (entry.key.equals(name)) {
                    return entry;
                }
            }
            throw new IllegalArgumentException("Entry not found for: " + name);
        }

        public void load(String resource) throws ServerException {
            Properties properties;
            try {
                /* Load defaults. */
                InputStream in = resourceLoader.getResource("classpath:defaults.properties").getInputStream();
                Properties defaults = new Properties();
                defaults.load(in);
                in.close();

                in = resourceLoader.getResource(resource).getInputStream();
                properties = new Properties(defaults);
                properties.load(in);
                in.close();
            } catch (IOException e) {
                throw new ServerException(PortalError.MISSING_PWS_CONFIGURATION, "read configuration failed.");
            }

            validate(properties);
        }

        void validate(Properties properties) throws ServerException {

            for (Entry entry : entries) {
                String value = properties.getProperty(entry.key);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                entry.parseValue(value);
            }
        }
    }

    enum ValueType {
        BOOLEAN(Boolean.class),
        INTEGER(Integer.class),
        STRING(String.class),
        SEVERITY(ActivityConfiguration.Severity.class);

        private final Class<?> cls;

        ValueType(Class<?> cls) {
            this.cls = cls;
        }
    }


    public static class Entry {
        String key;
        ValueType valueType;
        Object value;

        void parseValue(String value) {
            switch (valueType) {
                case BOOLEAN:
                    this.value = Boolean.parseBoolean(value);
                    break;

                case INTEGER:
                    this.value = Integer.parseInt(value);
                    break;
                case STRING:
                    this.value = value;
                    break;

                case SEVERITY:
                    this.value = ActivityConfiguration.Severity.valueOf(value);
                    break;
            }
        }

        @SuppressWarnings("unchecked")
        <T> T getValue() {
            return (T) valueType.cls.cast(value);
        }

        static Entry of(String key, ValueType valueType) {
            Entry entry = new Entry();
            entry.key = key;
            entry.valueType = valueType;
            return entry;
        }
    }
}