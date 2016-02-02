package cn.com.xinli.portal.core.configuration;

/**
 * Server configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class ServerConfiguration {
    private String privateKey;
    private String mainPageRedirectUrl;
    private boolean allowNat;
    private boolean enableCluster;
    private boolean enableRateLimiting;

    private ActivityConfiguration activityConfiguration;
    private ClusterConfiguration clusterConfiguration;
    private PortalServerConfiguration portalServerConfiguration;
    private RateLimitingConfiguration rateLimitingConfiguration;
    private RestConfiguration restConfiguration;
    private SessionConfiguration sessionConfiguration;

    public boolean isEnableCluster() {
        return enableCluster;
    }

    public void setEnableCluster(boolean enableCluster) {
        this.enableCluster = enableCluster;
    }

    public boolean isEnableRateLimiting() {
        return enableRateLimiting;
    }

    public void setEnableRateLimiting(boolean enableRateLimiting) {
        this.enableRateLimiting = enableRateLimiting;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getMainPageRedirectUrl() {
        return mainPageRedirectUrl;
    }

    public void setMainPageRedirectUrl(String mainPageRedirectUrl) {
        this.mainPageRedirectUrl = mainPageRedirectUrl;
    }

    public boolean isAllowNat() {
        return allowNat;
    }

    public void setAllowNat(boolean allowNat) {
        this.allowNat = allowNat;
    }

    public ActivityConfiguration getActivityConfiguration() {
        return activityConfiguration;
    }

    public void setActivityConfiguration(ActivityConfiguration activityConfiguration) {
        this.activityConfiguration = activityConfiguration;
    }

    public ClusterConfiguration getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    public SessionConfiguration getSessionConfiguration() {
        return sessionConfiguration;
    }

    public RestConfiguration getRestConfiguration() {
        return restConfiguration;
    }

    public void setRestConfiguration(RestConfiguration restConfiguration) {
        this.restConfiguration = restConfiguration;
    }

    public RateLimitingConfiguration getRateLimitingConfiguration() {
        return rateLimitingConfiguration;
    }

    public void setRateLimitingConfiguration(RateLimitingConfiguration rateLimitingConfiguration) {
        this.rateLimitingConfiguration = rateLimitingConfiguration;
    }

    public void setSessionConfiguration(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }

    public PortalServerConfiguration getPortalServerConfiguration() {
        return portalServerConfiguration;
    }

    public void setPortalServerConfiguration(PortalServerConfiguration portalServerConfiguration) {
        this.portalServerConfiguration = portalServerConfiguration;
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "privateKey='" + privateKey + '\'' +
                ", mainPageRedirectUrl='" + mainPageRedirectUrl + '\'' +
                ", allowNat=" + allowNat +
                ", enableCluster=" + enableCluster +
                ", enableRateLimiting=" + enableRateLimiting +
                ", activityConfiguration=" + activityConfiguration +
                ", clusterConfiguration=" + clusterConfiguration +
                ", portalServerConfiguration=" + portalServerConfiguration +
                ", rateLimitingConfiguration=" + rateLimitingConfiguration +
                ", restConfiguration=" + restConfiguration +
                ", sessionConfiguration=" + sessionConfiguration +
                '}';
    }
}
