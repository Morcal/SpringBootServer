package cn.com.xinli.portal.core.configuration;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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

    /** App download configurations. */
    public static final String APP_IOS_FILEPATH = "ios.app.filepath";
    public static final String APP_ANDROID_FILEPATH = "android.app.filepath";
    public static final String APP_MAC_FILEPATH = "mac.app.filepath";
    public static final String APP_LINUX_FILEPATH = "linux.app.filepath";
    public static final String APP_WINDOWS_FILEPATH = "windows.app.filepath";

    /** App configurations. */
    public static final String APP_IOS_VERSION = "ios.app.version";
    public static final String APP_ANDROID_VERSION = "android.app.version";
    public static final String APP_MAC_VERSION = "mac.app.version";
    public static final String APP_LINUX_VERSION = "linux.app.version";
    public static final String APP_WINDOWS_VERSION = "windows.app.version";

    /** PIN code configurations. */
    public static final String PIN_REQUIRED = "credentials.pin.required";
    public static final String PIN_PREFIX = "credentials.pin.prefix";
    public static final String PIN_SHARED_KEY = "credentials.pin.shared-key";

    /** Internal configuration entries. */
    private final Map<String, ConfigurationEntry> entries = new HashMap<>();

    /** Configuration metadata. */
    private static final EntryMetadata[] metadata = {
            /* Server private key. */
            EntryMetadata.of(SERVER_PRIVATE_KEY, ValueType.STRING),
            EntryMetadata.of(SERVER_CHECK_REDIRECT_URL, ValueType.BOOLEAN),
            EntryMetadata.of(SERVER_ADMIN_DEFAULT_USERNAME, ValueType.STRING),
            EntryMetadata.of(SERVER_ADMIN_DEFAULT_PASSWORD, ValueType.STRING),
            /* Main page redirect url. */
            EntryMetadata.of(MAIN_PAGE_REDIRECT_URL, ValueType.STRING),
            /* Allow nat. */
            EntryMetadata.of(NAT_ALLOWED, ValueType.BOOLEAN),
            /* Rest configurations. */
            EntryMetadata.of(REST_HOST, ValueType.STRING),
            EntryMetadata.of(REST_SERVER, ValueType.STRING),
            EntryMetadata.of(REST_SCHEME, ValueType.STRING),
            EntryMetadata.of(REST_HEADER, ValueType.STRING),
            EntryMetadata.of(REST_META, ValueType.STRING),
            EntryMetadata.of(REST_CHALLENGE_TTL, ValueType.INTEGER),
            EntryMetadata.of(REST_TOKEN_TTL, ValueType.INTEGER),
            /* Cluster configurations. */
            EntryMetadata.of(CLUSTER_ENABLED, ValueType.BOOLEAN),
            EntryMetadata.of(CLUSTER_REDIS_MASTER, ValueType.STRING),
            EntryMetadata.of(CLUSTER_REDIS_SENTINELS, ValueType.STRING),
            /* Activity configurations. */
            EntryMetadata.of(ACTIVITY_MOST_RECENT, ValueType.INTEGER),
            EntryMetadata.of(ACTIVITY_LOGGING_MIN_SEVERITY, ValueType.SEVERITY),
            /* Session configurations. */
            EntryMetadata.of(SESSION_TTL_ENABLED, ValueType.BOOLEAN),
            EntryMetadata.of(SESSION_TTL_VALUE, ValueType.INTEGER),
            EntryMetadata.of(SESSION_TOKEN_TTL, ValueType.INTEGER),
            EntryMetadata.of(SESSION_HEARTBEAT_ENABLED, ValueType.BOOLEAN),
            EntryMetadata.of(SESSION_HEARTBEAT_INTERVAL, ValueType.INTEGER),
            EntryMetadata.of(SESSION_UPDATE_MIN_INTERVAL, ValueType.INTEGER),
            EntryMetadata.of(SESSION_REMOVE_QUEUE_MAX_LENGTH, ValueType.INTEGER),
            /* Rate-limiting configurations. */
            EntryMetadata.of(RATE_LIMITING_ENABLED, ValueType.BOOLEAN),
            EntryMetadata.of(RATE_LIMITING_RATE, ValueType.INTEGER),
            EntryMetadata.of(RATE_LIMITING_TTL, ValueType.INTEGER),
            /* Portal server configurations. */
            EntryMetadata.of(PORTAL_SERVER_NAME, ValueType.STRING),
            EntryMetadata.of(PORTAL_SERVER_HOST, ValueType.STRING),
            EntryMetadata.of(PORTAL_SERVER_PROTOCOL_VERSION, ValueType.STRING),
            EntryMetadata.of(PORTAL_SERVER_LISTEN_PORT, ValueType.INTEGER),
            EntryMetadata.of(PORTAL_SERVER_CORE_THREADS, ValueType.INTEGER),
            EntryMetadata.of(PORTAL_SERVER_SHARED_SECRET, ValueType.STRING),
            /* redirect configurations. */
            EntryMetadata.of(REDIRECT_USER_IP, ValueType.STRING),
            EntryMetadata.of(REDIRECT_USER_MAC, ValueType.STRING),
            EntryMetadata.of(REDIRECT_NAS_IP, ValueType.STRING),
            /* App download configurations. */
            EntryMetadata.of(APP_ANDROID_FILEPATH, ValueType.STRING),
            EntryMetadata.of(APP_IOS_FILEPATH, ValueType.STRING),
            EntryMetadata.of(APP_MAC_FILEPATH, ValueType.STRING),
            EntryMetadata.of(APP_LINUX_FILEPATH, ValueType.STRING),
            EntryMetadata.of(APP_WINDOWS_FILEPATH, ValueType.STRING),
            /* App configurations. */
            EntryMetadata.of(APP_ANDROID_VERSION, ValueType.STRING),
            EntryMetadata.of(APP_IOS_VERSION, ValueType.STRING),
            EntryMetadata.of(APP_MAC_VERSION, ValueType.STRING),
            EntryMetadata.of(APP_LINUX_VERSION, ValueType.STRING),
            EntryMetadata.of(APP_WINDOWS_VERSION, ValueType.STRING),
            /* PIN code configurations. */
            EntryMetadata.of(PIN_REQUIRED, ValueType.BOOLEAN),
            EntryMetadata.of(PIN_PREFIX, ValueType.STRING),
            EntryMetadata.of(PIN_SHARED_KEY, ValueType.STRING),
    };

    public static Collection<String> keys() {
        return Stream.of(metadata).map(EntryMetadata::getKey).collect(Collectors.toList());
    }

    /**
     * Get configuration entry value.
     * @param entry entry name.
     * @param <T> value type.
     * @return entry value.
     */
    public <T>T getEntryValue(String entry) throws ServerConfigurationNotExistsException {
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
        if (!entries.containsKey(name)) {
            throw new ServerConfigurationNotExistsException(name);
        }

        return entries.get(name);
    }

    public static Optional<EntryMetadata> getMetadata(String key) {
        return Stream.of(metadata).filter(m -> m.getKey().equals(key)).findFirst();
    }

    public void setEntries(Iterable<ConfigurationEntry> entries) {
        entries.forEach(this::addEntry);
    }

    private void addEntry(ConfigurationEntry entry) {
        Objects.requireNonNull(entry, "Entry can not be null.");

        entry.updateValue(entry.getValueText());
        entries.put(entry.getKey(), entry);
    }

    /**
     * Load default configurations.
     * @return properties.
     * @throws ServerException
     */
    public static Properties loadDefaults() throws ServerException {
        InputStream in = null;
        try {
            in = Configuration.class.getClassLoader().getResourceAsStream("defaults.properties");
            Properties defaults = new Properties();
            defaults.load(in);
            in.close();
            return defaults;
        } catch (IOException e) {
            throw new ServerException(PortalError.MISSING_SERVER_CONFIGURATION,
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

    public enum ValueType {
        BOOLEAN(Boolean.class),
        INTEGER(Integer.class),
        STRING(String.class),
        SEVERITY(Activity.Severity.class);

        private final Class<?> valueClass;

        ValueType(Class<?> cls) {
            this.valueClass = cls;
        }

        @SuppressWarnings("unchecked")
        public <T>T cast(Object object) {
            return (T) valueClass.cast(object);
        }
    }
    
    public static class EntryMetadata {
        private String key;
        private ValueType valueType;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public void setValueType(ValueType valueType) {
            this.valueType = valueType;
        }

        static EntryMetadata of (String key, ValueType valueType) {
            EntryMetadata metadata = new EntryMetadata();
            metadata.setKey(key);
            metadata.setValueType(valueType);
            return metadata;
        }
    }
}
