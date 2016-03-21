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
public class PropertiesServerConfiguration extends ServerConfiguration {
    /** Server private key. */
    public static final String SERVER_PRIVATE_KEY = "server.private-key";
    public static final String SERVER_CHECK_REDIRECT_URL = "server.check-redirect-url";
    public static final String SERVER_ADMIN_DEFAULT_USERNAME = "server.admin.default.username";
    public static final String SERVER_ADMIN_DEFAULT_PASSWORD = "server.admin.default.password.md5";
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
    public static final String SESSION_REMOVE_QUEUE_MAX_LENGTH = "session.remove.queue.max-length";
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
    /** Redirect configurations. */
    public static final String REDIRECT_USER_IP = "redirect.user.ip";
    public static final String REDIRECT_USER_MAC = "redirect.user.mac";
    public static final String REDIRECT_NAS_IP = "redirect.nas.ip";

    private static final String PROPERTIES_RESOURCE = "pws.properties";

//    @Autowired
//    private ResourceLoader resourceLoader;

    public PropertiesServerConfiguration() throws ServerException {
        loadFromProperties();
    }

    /**
     * Load configuration from properties.
     * @throws ServerException
     */
    private void loadFromProperties() throws ServerException {
        Configuration config = new Configuration();
        config.load(PROPERTIES_RESOURCE);

        setPrivateKey(config.valueOf(SERVER_PRIVATE_KEY));
        setCheckRedirectUrl(config.valueOf(SERVER_CHECK_REDIRECT_URL));
        setMainPageRedirectUrl(config.valueOf(MAIN_PAGE_REDIRECT_URL));
        setAllowNat(config.valueOf(NAT_ALLOWED));
        setDefaultAdminUsername(config.valueOf(SERVER_ADMIN_DEFAULT_USERNAME));
        setDefaultAdminPassword(config.valueOf(SERVER_ADMIN_DEFAULT_PASSWORD));

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config.valueOf(ACTIVITY_MOST_RECENT));
        activity.setSeverity(config.valueOf(ACTIVITY_LOGGING_MIN_SEVERITY));
        setActivityConfiguration(activity);

        /* Create session configuration. */
        SessionConfiguration session = new SessionConfiguration();
        session.setEnableHeartbeat(config.valueOf(SESSION_HEARTBEAT_ENABLED));
        session.setTokenTtl(config.valueOf(SESSION_TOKEN_TTL));
        session.setRemoveQueueMaxLength(config.valueOf(SESSION_REMOVE_QUEUE_MAX_LENGTH));
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

        /* Create redirect configuration. */
        RedirectConfiguration redirectConfiguration = new RedirectConfiguration();
        String nasIp = config.valueOf(REDIRECT_NAS_IP);
        redirectConfiguration.setNasIp(nasIp.split(","));
        String userIp = config.valueOf(REDIRECT_USER_IP);
        redirectConfiguration.setUserIp(userIp.split(","));
        String userMac = config.valueOf(REDIRECT_USER_MAC);
        redirectConfiguration.setUserMac(userMac.split(","));
        setRedirectConfiguration(redirectConfiguration);
    }

    class Configuration {
        Entry[] entries = {
                /* Server private key. */
                Entry.of(SERVER_PRIVATE_KEY, ValueType.STRING),
                Entry.of(SERVER_CHECK_REDIRECT_URL, ValueType.BOOLEAN),
                Entry.of(SERVER_ADMIN_DEFAULT_USERNAME, ValueType.STRING),
                Entry.of(SERVER_ADMIN_DEFAULT_PASSWORD, ValueType.STRING),
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
                Entry.of(SESSION_REMOVE_QUEUE_MAX_LENGTH, ValueType.INTEGER),
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
                /* redirect configurations. */
                Entry.of(REDIRECT_USER_IP, ValueType.STRING),
                Entry.of(REDIRECT_USER_MAC, ValueType.STRING),
                Entry.of(REDIRECT_NAS_IP, ValueType.STRING),
        };

        /**
         * Get configuration entry value.
         * @param entry entry name.
         * @param <T> value type.
         * @return entry value.
         */
        <T>T valueOf(String entry) {
            return getEntry(entry).getValue();
        }

        /**
         * Get configuration entry.
         * @param name entry name.
         * @return configuration enty.
         */
        public Entry getEntry(String name) {
            for (Entry entry : entries) {
                if (entry.key.equals(name)) {
                    return entry;
                }
            }
            throw new IllegalArgumentException("Entry not found for: " + name);
        }

        /**
         * Load configuration from resource.
         * @param resource resource name.
         * @throws ServerException
         */
        public void load(String resource) throws ServerException {
            Properties properties;
            InputStream in = null;
            try {
                /* Load defaults. */
                in = getClass().getClassLoader().getResourceAsStream("defaults.properties");
                Properties defaults = new Properties();
                defaults.load(in);
                in.close();

                //in = resourceLoader.getResource(resource).getInputStream();
                in = getClass().getClassLoader().getResourceAsStream(resource);
                properties = new Properties(defaults);
                properties.load(in);
                in.close();
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

            validate(properties);
        }

        /**
         * Validate specified {@code properties}.
         * @param properties properties.
         * @throws ServerException
         */
        void validate(Properties properties) throws ServerException {
            for (Entry entry : entries) {
                String value = properties.getProperty(entry.key);
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                entry.readValue(value);
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

    /**
     * Configuration entry.
     */
    public static class Entry {
        String key;
        ValueType valueType;
        Object value;

        /**
         * Read value into this entry.
         * @param value value.
         */
        void readValue(String value) {
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

        /**
         * Get entry value.
         * @param <T> value type.
         * @return entry value.
         */
        @SuppressWarnings("unchecked")
        <T> T getValue() {
            return (T) valueType.cls.cast(value);
        }

        /**
         * Create entry from key and value type.
         * @param key entry key.
         * @param valueType entry value type.
         * @return entry.
         */
        static Entry of(String key, ValueType valueType) {
            Entry entry = new Entry();
            entry.key = key;
            entry.valueType = valueType;
            return entry;
        }
    }
}
