package cn.com.xinli.portal.core.configuration.support;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.*;

/**
 * Provides a java properties configuration file based
 * portal server configuration implementation.
 *
 * <p>{@link cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration}
 * load a default configurations if system not configured yet.
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
    public void load(Configuration config) throws ServerException {
        setPrivateKey(config.getEntryValue(Configuration.SERVER_PRIVATE_KEY));
        setCheckRedirectUrl(config.getEntryValue(Configuration.SERVER_CHECK_REDIRECT_URL));
        setMainPageRedirectUrl(config.getEntryValue(Configuration.MAIN_PAGE_REDIRECT_URL));
        setAllowNat(config.getEntryValue(Configuration.NAT_ALLOWED));
        setDefaultAdminUsername(config.getEntryValue(Configuration.SERVER_ADMIN_DEFAULT_USERNAME));
        setDefaultAdminPassword(config.getEntryValue(Configuration.SERVER_ADMIN_DEFAULT_PASSWORD));

        /* Create activity configuration. */
        ActivityConfiguration activity = new ActivityConfiguration();
        activity.setMostRecent(config.getEntryValue(Configuration.ACTIVITY_MOST_RECENT));
        activity.setMinimumSeverity(config.getEntryValue(Configuration.ACTIVITY_LOGGING_MIN_SEVERITY));
        setActivityConfiguration(activity);

        /* Create session configuration. */
        SessionConfiguration session = new SessionConfiguration();
        session.setEnableHeartbeat(config.getEntryValue(Configuration.SESSION_HEARTBEAT_ENABLED));
        session.setTokenTtl(config.getEntryValue(Configuration.SESSION_TOKEN_TTL));
        session.setRemoveQueueMaxLength(config.getEntryValue(Configuration.SESSION_REMOVE_QUEUE_MAX_LENGTH));
        session.setHeartbeatInterval(config.getEntryValue(Configuration.SESSION_HEARTBEAT_INTERVAL));
        session.setEnableTtl(config.getEntryValue(Configuration.SESSION_TTL_ENABLED));
        session.setTtl(config.getEntryValue(Configuration.SESSION_TTL_VALUE));
        setSessionConfiguration(session);

        /* Create portal server configuration(s). */
        PortalServerConfiguration internal = new PortalServerConfiguration();
        internal.setHost(config.getEntryValue(Configuration.PORTAL_SERVER_HOST));
        internal.setName(config.getEntryValue(Configuration.PORTAL_SERVER_NAME));
        internal.setPort(config.getEntryValue(Configuration.PORTAL_SERVER_LISTEN_PORT));
        internal.setVersion(config.getEntryValue(Configuration.PORTAL_SERVER_PROTOCOL_VERSION));
        internal.setSharedSecret(config.getEntryValue(Configuration.PORTAL_SERVER_SHARED_SECRET));
        setPortalServerConfiguration(internal);

        /* Create rate limiting configuration. */
        setEnableRateLimiting(config.getEntryValue(Configuration.RATE_LIMITING_ENABLED));
        RateLimitingConfiguration rate = new RateLimitingConfiguration();
        rate.setRate(config.getEntryValue(Configuration.RATE_LIMITING_RATE));
        rate.setTtl(config.getEntryValue(Configuration.RATE_LIMITING_TTL));
        setRateLimitingConfiguration(rate);

        /* Create rest configuration. */
        RestConfiguration rest = new RestConfiguration();
        rest.setChallengeTtl(config.getEntryValue(Configuration.REST_CHALLENGE_TTL));
        rest.setHeader(config.getEntryValue(Configuration.REST_HEADER));
        rest.setHost(config.getEntryValue(Configuration.REST_HOST));
        rest.setServer(config.getEntryValue(Configuration.REST_SERVER));
        rest.setMeta(config.getEntryValue(Configuration.REST_META));
        rest.setScheme(config.getEntryValue(Configuration.REST_SCHEME));
        rest.setTokenTtl(config.getEntryValue(Configuration.REST_TOKEN_TTL));
        setRestConfiguration(rest);

        /* Create cluster configuration. */
        setEnableCluster(config.getEntryValue(Configuration.CLUSTER_ENABLED));
        ClusterConfiguration cluster = new ClusterConfiguration();
        cluster.setRedisMaster(config.getEntryValue(Configuration.CLUSTER_REDIS_MASTER));
        String sentinels = config.getEntryValue(Configuration.CLUSTER_REDIS_SENTINELS);
        cluster.setRedisSentinels(sentinels.split(","));
        setClusterConfiguration(cluster);

        /* Create redirect configuration. */
        RedirectConfiguration redirectConfiguration = new RedirectConfiguration();
        String nasIp = config.getEntryValue(Configuration.REDIRECT_NAS_IP);
        redirectConfiguration.setNasIp(nasIp.split(","));
        String userIp = config.getEntryValue(Configuration.REDIRECT_USER_IP);
        redirectConfiguration.setUserIp(userIp.split(","));
        String userMac = config.getEntryValue(Configuration.REDIRECT_USER_MAC);
        redirectConfiguration.setUserMac(userMac.split(","));
        setRedirectConfiguration(redirectConfiguration);

        /* Create app configuration. */
        AppConfiguration app = new AppConfiguration();
        app.setiOSAppFileName(config.getEntryValue(Configuration.DOWNLOAD_IOS_APP));
        app.setAndroidAppFileName(config.getEntryValue(Configuration.DOWNLOAD_ANDROID_APP));
        app.setLinuxAppFileName(config.getEntryValue(Configuration.DOWNLOAD_LINUX_APP));
        app.setMacAppFileName(config.getEntryValue(Configuration.DOWNLOAD_MAC_APP));
        app.setWindowsAppFileName(config.getEntryValue(Configuration.DOWNLOAD_WINDOWS_APP));
        setAppConfiguration(app);
    }
}
