package cn.com.xinli.portal.core.configuration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Server configuration.
 * @author zhoupeng, created on 2016/3/31.
 */
public class Configuration {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Configuration.class);

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

    private static final ConfigurationEntry[] entries = {
                /* Server private key. */
            ConfigurationEntry.of(SERVER_PRIVATE_KEY, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(SERVER_CHECK_REDIRECT_URL, ConfigurationEntry.ValueType.BOOLEAN),
            ConfigurationEntry.of(SERVER_ADMIN_DEFAULT_USERNAME, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(SERVER_ADMIN_DEFAULT_PASSWORD, ConfigurationEntry.ValueType.STRING),
                /* Main page redirect url. */
            ConfigurationEntry.of(MAIN_PAGE_REDIRECT_URL, ConfigurationEntry.ValueType.STRING),
                /* Allow nat. */
            ConfigurationEntry.of(NAT_ALLOWED, ConfigurationEntry.ValueType.BOOLEAN),
                /* Rest configurations. */
            ConfigurationEntry.of(REST_HOST, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REST_SERVER, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REST_SCHEME, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REST_HEADER, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REST_META, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REST_CHALLENGE_TTL, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(REST_TOKEN_TTL, ConfigurationEntry.ValueType.INTEGER),
                /* Cluster configurations. */
            ConfigurationEntry.of(CLUSTER_ENABLED, ConfigurationEntry.ValueType.BOOLEAN),
            ConfigurationEntry.of(CLUSTER_REDIS_MASTER, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(CLUSTER_REDIS_SENTINELS, ConfigurationEntry.ValueType.STRING),
                /* Activity configurations. */
            ConfigurationEntry.of(ACTIVITY_MOST_RECENT, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(ACTIVITY_LOGGING_MIN_SEVERITY, ConfigurationEntry.ValueType.SEVERITY),
                /* Session configurations. */
            ConfigurationEntry.of(SESSION_TTL_ENABLED, ConfigurationEntry.ValueType.BOOLEAN),
            ConfigurationEntry.of(SESSION_TTL_VALUE, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(SESSION_TOKEN_TTL, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(SESSION_HEARTBEAT_ENABLED, ConfigurationEntry.ValueType.BOOLEAN),
            ConfigurationEntry.of(SESSION_HEARTBEAT_INTERVAL, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(SESSION_UPDATE_MIN_INTERVAL, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(SESSION_REMOVE_QUEUE_MAX_LENGTH, ConfigurationEntry.ValueType.INTEGER),
                /* Rate-limiting configurations. */
            ConfigurationEntry.of(RATE_LIMITING_ENABLED, ConfigurationEntry.ValueType.BOOLEAN),
            ConfigurationEntry.of(RATE_LIMITING_RATE, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(RATE_LIMITING_TTL, ConfigurationEntry.ValueType.INTEGER),
                /* Portal server configurations. */
            ConfigurationEntry.of(PORTAL_SERVER_NAME, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(PORTAL_SERVER_HOST, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(PORTAL_SERVER_PROTOCOL_VERSION, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(PORTAL_SERVER_LISTEN_PORT, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(PORTAL_SERVER_CORE_THREADS, ConfigurationEntry.ValueType.INTEGER),
            ConfigurationEntry.of(PORTAL_SERVER_SHARED_SECRET, ConfigurationEntry.ValueType.STRING),
                /* redirect configurations. */
            ConfigurationEntry.of(REDIRECT_USER_IP, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REDIRECT_USER_MAC, ConfigurationEntry.ValueType.STRING),
            ConfigurationEntry.of(REDIRECT_NAS_IP, ConfigurationEntry.ValueType.STRING),
    };

    public static Collection<String> keys() {
        return Stream.of(entries).map(ConfigurationEntry::getKey).collect(Collectors.toList());
    }

    /**
     * Get configuration entry value.
     * @param entry entry name.
     * @param <T> value type.
     * @return entry value.
     */
    public <T>T valueOf(String entry) throws ServerConfigurationNotExistsException {
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
     * @return configuration entry.
     */
    public ConfigurationEntry getEntry(String name) throws ServerConfigurationNotExistsException {
        for (ConfigurationEntry entry : entries) {
            if (entry.getKey().equals(name)) {
                return entry;
            }
        }
        throw new ServerConfigurationNotExistsException(name);
    }

    /**
     * Load configuration from properties.
     * @param properties properties.
     */
    public void load(Properties properties) {
        for (ConfigurationEntry entry : entries) {
            String value = properties.getProperty(entry.getKey());
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            entry.readValue(value);
        }
    }

    public Iterable<ConfigurationEntry> entries() {
        return Collections.unmodifiableList(Arrays.asList(entries));
    }
}
