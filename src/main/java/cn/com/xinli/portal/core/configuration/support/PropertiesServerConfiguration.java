package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.configuration.*;
import org.apache.commons.lang3.StringUtils;

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
//@Component
public class PropertiesServerConfiguration extends ServerConfiguration {
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
    public static final String REST_TOKEN_TTL = "rest.token.ttl";
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
    public static final String SESSION_TOKEN_TTL = "session.token.ttl";
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

    private static final String PROPERTIES_RESOURCE = "pws.properties";

//    @Autowired
//    private ResourceLoader resourceLoader;

    public PropertiesServerConfiguration() throws ServerException {
        loadFromProperties();
    }

    private void loadFromProperties() throws ServerException {
        Configuration config = new Configuration();
        config.load(PROPERTIES_RESOURCE);

        setPrivateKey(config.valueOf(SERVER_PRIVATE_KEY));
        setMainPageRedirectUrl(config.valueOf(MAIN_PAGE_REDIRECT_URL));
        setAllowNat(config.valueOf(NAT_ALLOWED));

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config.valueOf(ACTIVITY_MOST_RECENT));
        activity.setSeverity(config.valueOf(ACTIVITY_LOGGING_MIN_SEVERITY));
        setActivityConfiguration(activity);

        /* Create session configuration. */
        SessionConfiguration session = new SessionConfiguration();
        session.setEnableHeartbeat(config.valueOf(SESSION_HEARTBEAT_ENABLED));
        session.setTokenTtl(config.valueOf(SESSION_TOKEN_TTL));
        if (session.isEnableHeartbeat()) {
            session.setHeartbeatInterval(config.valueOf(SESSION_HEARTBEAT_INTERVAL));
        }
        session.setEnableTtl(config.valueOf(SESSION_TTL_ENABLED));
        if (session.isEnableTtl()) {
            session.setTtl(config.valueOf(SESSION_TTL_VALUE));
        }
        setSessionConfiguration(session);

        /* Create portal server configuration(s). */
        PortalServerConfiguration internal = new PortalServerConfiguration();
        internal.setHost(config.valueOf(PORTAL_SERVER_HOST));
        internal.setName(config.valueOf(PORTAL_SERVER_NAME));
        internal.setPort(config.valueOf(PORTAL_SERVER_LISTEN_PORT));
        internal.setVersion(config.valueOf(PORTAL_SERVER_PROTOCOL_VERSION));
        internal.setSharedSecret(config.valueOf(PORTAL_SERVER_SHARED_SECRET));
        setPortalServerConfiguration(internal);

        /* Create rate limiting configuration. */
        setEnableRateLimiting(config.valueOf(RATE_LIMITING_ENABLED));
        if (isEnableRateLimiting()) {
            RateLimitingConfiguration rate = new RateLimitingConfiguration();
            rate.setRate(config.valueOf(RATE_LIMITING_RATE));
            rate.setTtl(config.valueOf(RATE_LIMITING_TTL));
            setRateLimitingConfiguration(rate);
        }

        /* Create rest configuration. */
        RestConfiguration rest = new RestConfiguration();
        rest.setChallengeTtl(config.valueOf(REST_CHALLENGE_TTL));
        rest.setHeader(config.valueOf(REST_HEADER));
        rest.setHost(config.valueOf(REST_HOST));
        rest.setServer(config.valueOf(REST_SERVER));
        rest.setMeta(config.valueOf(REST_META));
        rest.setScheme(config.valueOf(REST_SCHEME));
        rest.setTokenTtl(config.valueOf(REST_TOKEN_TTL));
        setRestConfiguration(rest);

        /* Create cluster configuration. */
        setEnableCluster(config.valueOf(CLUSTER_ENABLED));
        if (isEnableCluster()) {
            ClusterConfiguration cluster = new ClusterConfiguration();
            cluster.setRedisMaster(config.valueOf(CLUSTER_REDIS_MASTER));
            String sentinels = config.valueOf(CLUSTER_REDIS_SENTINELS);
            cluster.setRedisSentinels(sentinels.split(","));
            setClusterConfiguration(cluster);
        }
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
                Entry.of(REST_TOKEN_TTL, ValueType.INTEGER),
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
                Entry.of(SESSION_TOKEN_TTL, ValueType.INTEGER),
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
//                InputStream in = resourceLoader.getResource("classpath:defaults.properties").getInputStream();
                InputStream in = getClass().getClassLoader().getResourceAsStream("defaults.properties");
                Properties defaults = new Properties();
                defaults.load(in);
                in.close();

                //in = resourceLoader.getResource(resource).getInputStream();
                in = getClass().getClassLoader().getResourceAsStream(resource);
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
        SEVERITY(Activity.Severity.class);

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
                    this.value = Activity.Severity.valueOf(value);
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
