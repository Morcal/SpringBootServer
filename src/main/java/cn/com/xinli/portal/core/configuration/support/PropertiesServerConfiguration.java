package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static final String SERVER_ADMIN_DEFAULT_PASSWORD = "server.admin.default.password";
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
    public static final String ACTIVITY_LOGGING_MIN_SEVERITY = "activity.logging.minimum.severity";
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

    /**
     * Load configuration from properties.
     * @throws ServerException
     */
    public void load(Configuration config) throws ServerException {
        setPrivateKey(config.valueOf(SERVER_PRIVATE_KEY));
        setCheckRedirectUrl(config.valueOf(SERVER_CHECK_REDIRECT_URL));
        setMainPageRedirectUrl(config.valueOf(MAIN_PAGE_REDIRECT_URL));
        setAllowNat(config.valueOf(NAT_ALLOWED));
        setDefaultAdminUsername(config.valueOf(SERVER_ADMIN_DEFAULT_USERNAME));
        setDefaultAdminPassword(config.valueOf(SERVER_ADMIN_DEFAULT_PASSWORD));

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config.valueOf(ACTIVITY_MOST_RECENT));
        activity.setMinimumSevertiy(config.valueOf(ACTIVITY_LOGGING_MIN_SEVERITY));
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

    public static class Configuration {
        /** Logger. */
        private final Logger logger = LoggerFactory.getLogger(Configuration.class);

        private static final ServerConfigurationEntry[] entries = {
                /* Server private key. */
                ServerConfigurationEntry.of(SERVER_PRIVATE_KEY, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(SERVER_CHECK_REDIRECT_URL, ServerConfigurationEntry.ValueType.BOOLEAN),
                ServerConfigurationEntry.of(SERVER_ADMIN_DEFAULT_USERNAME, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(SERVER_ADMIN_DEFAULT_PASSWORD, ServerConfigurationEntry.ValueType.STRING),
                /* Main page redirect url. */
                ServerConfigurationEntry.of(MAIN_PAGE_REDIRECT_URL, ServerConfigurationEntry.ValueType.STRING),
                /* Allow nat. */
                ServerConfigurationEntry.of(NAT_ALLOWED, ServerConfigurationEntry.ValueType.BOOLEAN),
                /* Rest configurations. */
                ServerConfigurationEntry.of(REST_HOST, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REST_SERVER, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REST_SCHEME, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REST_HEADER, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REST_META, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REST_CHALLENGE_TTL, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(REST_TOKEN_TTL, ServerConfigurationEntry.ValueType.INTEGER),
                /* Cluster configurations. */
                ServerConfigurationEntry.of(CLUSTER_ENABLED, ServerConfigurationEntry.ValueType.BOOLEAN),
                ServerConfigurationEntry.of(CLUSTER_REDIS_MASTER, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(CLUSTER_REDIS_SENTINELS, ServerConfigurationEntry.ValueType.STRING),
                /* Activity configurations. */
                ServerConfigurationEntry.of(ACTIVITY_MOST_RECENT, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(ACTIVITY_LOGGING_MIN_SEVERITY, ServerConfigurationEntry.ValueType.SEVERITY),
                /* Session configurations. */
                ServerConfigurationEntry.of(SESSION_TTL_ENABLED, ServerConfigurationEntry.ValueType.BOOLEAN),
                ServerConfigurationEntry.of(SESSION_TTL_VALUE, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(SESSION_TOKEN_TTL, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(SESSION_HEARTBEAT_ENABLED, ServerConfigurationEntry.ValueType.BOOLEAN),
                ServerConfigurationEntry.of(SESSION_HEARTBEAT_INTERVAL, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(SESSION_UPDATE_MIN_INTERVAL, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(SESSION_REMOVE_QUEUE_MAX_LENGTH, ServerConfigurationEntry.ValueType.INTEGER),
                /* Rate-limiting configurations. */
                ServerConfigurationEntry.of(RATE_LIMITING_ENABLED, ServerConfigurationEntry.ValueType.BOOLEAN),
                ServerConfigurationEntry.of(RATE_LIMITING_RATE, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(RATE_LIMITING_TTL, ServerConfigurationEntry.ValueType.INTEGER),
                /* Portal server configurations. */
                ServerConfigurationEntry.of(PORTAL_SERVER_NAME, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(PORTAL_SERVER_HOST, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(PORTAL_SERVER_PROTOCOL_VERSION, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(PORTAL_SERVER_LISTEN_PORT, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(PORTAL_SERVER_CORE_THREADS, ServerConfigurationEntry.ValueType.INTEGER),
                ServerConfigurationEntry.of(PORTAL_SERVER_SHARED_SECRET, ServerConfigurationEntry.ValueType.STRING),
                /* redirect configurations. */
                ServerConfigurationEntry.of(REDIRECT_USER_IP, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REDIRECT_USER_MAC, ServerConfigurationEntry.ValueType.STRING),
                ServerConfigurationEntry.of(REDIRECT_NAS_IP, ServerConfigurationEntry.ValueType.STRING),
        };

        /**
         * Get configuration entry value.
         * @param entry entry name.
         * @param <T> value type.
         * @return entry value.
         */
        <T>T valueOf(String entry) {
            try {
                return getEntry(entry).getValue();
            } catch (Exception e) {
                logger.error("> Missing property {{}}", entry);
                throw e;
            }
        }

        /**
         * Get configuration entry.
         * @param name entry name.
         * @return configuration enty.
         */
        ServerConfigurationEntry getEntry(String name) {
            for (ServerConfigurationEntry entry : entries) {
                if (entry.getKey().equals(name)) {
                    return entry;
                }
            }
            throw new IllegalArgumentException("ServerConfigurationEntry not found for: " + name);
        }

        /**
         * Load configuration from properties.
         * @param properties properties.
         */
        public void load(Properties properties) {
            for (ServerConfigurationEntry entry : entries) {
                String value = properties.getProperty(entry.getKey());
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                entry.readValue(value);
            }
        }
    }

//    enum ServerConfigurationEntry.ValueType {
//        BOOLEAN(Boolean.class),
//        INTEGER(Integer.class),
//        STRING(String.class),
//        SEVERITY(Activity.Severity.class);
//
//        private final Class<?> cls;
//
//        ServerConfigurationEntry.ValueType(Class<?> cls) {
//            this.cls = cls;
//        }
//    }
//
//    /**
//     * Configuration entry.
//     */
//    public static class ServerConfigurationEntry {
//        String key;
//        ServerConfigurationEntry.ValueType valueType;
//        Object value;
//
//        /**
//         * Read value into this entry.
//         * @param value value.
//         */
//        void readValue(String value) {
//            switch (valueType) {
//                case BOOLEAN:
//                    this.value = Boolean.parseBoolean(value);
//                    break;
//
//                case INTEGER:
//                    this.value = Integer.parseInt(value);
//                    break;
//
//                case STRING:
//                    this.value = value;
//                    break;
//
//                case SEVERITY:
//                    this.value = Activity.Severity.valueOf(value);
//                    break;
//            }
//        }
//
//        /**
//         * Get entry value.
//         * @param <T> value type.
//         * @return entry value.
//         */
//        @SuppressWarnings("unchecked")
//        <T> T getValue() {
//            return (T) valueType.cls.cast(value);
//        }
//
//        /**
//         * Create entry from key and value type.
//         * @param key entry key.
//         * @param valueType entry value type.
//         * @return entry.
//         */
//        static ServerConfigurationEntry of(String key, ServerConfigurationEntry.ValueType valueType) {
//            ServerConfigurationEntry entry = new ServerConfigurationEntry();
//            entry.key = key;
//            entry.valueType = valueType;
//            return entry;
//        }
//    }
}
