package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;

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

    /**
     * Load configuration from properties.
     * @throws ServerException
     */
    public void load(Configuration config) throws ServerException, ServerConfigurationNotExistsException {
        setPrivateKey(config. valueOf(Configuration.SERVER_PRIVATE_KEY));
        setCheckRedirectUrl(config. valueOf(Configuration.SERVER_CHECK_REDIRECT_URL));
        setMainPageRedirectUrl(config. valueOf(Configuration.MAIN_PAGE_REDIRECT_URL));
        setAllowNat(config. valueOf(Configuration.NAT_ALLOWED));
        setDefaultAdminUsername(config. valueOf(Configuration.SERVER_ADMIN_DEFAULT_USERNAME));
        setDefaultAdminPassword(config. valueOf(Configuration.SERVER_ADMIN_DEFAULT_PASSWORD));

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config. valueOf(Configuration.ACTIVITY_MOST_RECENT));
        activity.setMinimumSevertiy(config. valueOf(Configuration.ACTIVITY_LOGGING_MIN_SEVERITY));
        setActivityConfiguration(activity);

        /* Create session configuration. */
        SessionConfiguration session = new SessionConfiguration();
        session.setEnableHeartbeat(config. valueOf(Configuration.SESSION_HEARTBEAT_ENABLED));
        session.setTokenTtl(config. valueOf(Configuration.SESSION_TOKEN_TTL));
        session.setRemoveQueueMaxLength(config. valueOf(Configuration.SESSION_REMOVE_QUEUE_MAX_LENGTH));
        if (session.isEnableHeartbeat()) {
            session.setHeartbeatInterval(config. valueOf(Configuration.SESSION_HEARTBEAT_INTERVAL));
        }
        session.setEnableTtl(config. valueOf(Configuration.SESSION_TTL_ENABLED));
        if (session.isEnableTtl()) {
            session.setTtl(config. valueOf(Configuration.SESSION_TTL_VALUE));
        }
        setSessionConfiguration(session);

        /* Create portal server configuration(s). */
        PortalServerConfiguration internal = new PortalServerConfiguration();
        internal.setHost(config. valueOf(Configuration.PORTAL_SERVER_HOST));
        internal.setName(config. valueOf(Configuration.PORTAL_SERVER_NAME));
        internal.setPort(config. valueOf(Configuration.PORTAL_SERVER_LISTEN_PORT));
        internal.setVersion(config. valueOf(Configuration.PORTAL_SERVER_PROTOCOL_VERSION));
        internal.setSharedSecret(config. valueOf(Configuration.PORTAL_SERVER_SHARED_SECRET));
        setPortalServerConfiguration(internal);

        /* Create rate limiting configuration. */
        setEnableRateLimiting(config. valueOf(Configuration.RATE_LIMITING_ENABLED));
        if (isEnableRateLimiting()) {
            RateLimitingConfiguration rate = new RateLimitingConfiguration();
            rate.setRate(config. valueOf(Configuration.RATE_LIMITING_RATE));
            rate.setTtl(config. valueOf(Configuration.RATE_LIMITING_TTL));
            setRateLimitingConfiguration(rate);
        }

        /* Create rest configuration. */
        RestConfiguration rest = new RestConfiguration();
        rest.setChallengeTtl(config. valueOf(Configuration.REST_CHALLENGE_TTL));
        rest.setHeader(config. valueOf(Configuration.REST_HEADER));
        rest.setHost(config. valueOf(Configuration.REST_HOST));
        rest.setServer(config. valueOf(Configuration.REST_SERVER));
        rest.setMeta(config. valueOf(Configuration.REST_META));
        rest.setScheme(config. valueOf(Configuration.REST_SCHEME));
        rest.setTokenTtl(config. valueOf(Configuration.REST_TOKEN_TTL));
        setRestConfiguration(rest);

        /* Create cluster configuration. */
        setEnableCluster(config. valueOf(Configuration.CLUSTER_ENABLED));
        if (isEnableCluster()) {
            ClusterConfiguration cluster = new ClusterConfiguration();
            cluster.setRedisMaster(config. valueOf(Configuration.CLUSTER_REDIS_MASTER));
            String sentinels = config. valueOf(Configuration.CLUSTER_REDIS_SENTINELS);
            cluster.setRedisSentinels(sentinels.split(","));
            setClusterConfiguration(cluster);
        }

        /* Create redirect configuration. */
        RedirectConfiguration redirectConfiguration = new RedirectConfiguration();
        String nasIp = config. valueOf(Configuration.REDIRECT_NAS_IP);
        redirectConfiguration.setNasIp(nasIp.split(","));
        String userIp = config. valueOf(Configuration.REDIRECT_USER_IP);
        redirectConfiguration.setUserIp(userIp.split(","));
        String userMac = config. valueOf(Configuration.REDIRECT_USER_MAC);
        redirectConfiguration.setUserMac(userMac.split(","));
        setRedirectConfiguration(redirectConfiguration);
    }
}
