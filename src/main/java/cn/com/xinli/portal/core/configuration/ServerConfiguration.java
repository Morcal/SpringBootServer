package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Server configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerConfiguration {
    /** Server private key. */
    @JsonProperty("private_key")
    private String privateKey;

    /** Server main page if need to redirect clients to another url. */
    @JsonProperty("main_page_redirect_url")
    private String mainPageRedirectUrl;

    /** Server allowed NAT environment. */
    @JsonProperty("allow_nat")
    private boolean allowNat;

    /** Server works in a cluster mode. */
    @JsonProperty("enable_cluster")
    private boolean enableCluster;

    /** Server enabled rate-limiting. */
    @JsonProperty("enable_rate_limiting")
    private boolean enableRateLimiting;

    /** Server should check redirect url when user request to connect. */
    @JsonProperty("check_redirect_url")
    private boolean checkRedirectUrl;

    @JsonProperty("default_admin_username")
    private String defaultAdminUsername;

    @JsonProperty("default_admin_password")
    private String defaultAdminPassword;

    @JsonProperty("activity")
    private ActivityConfiguration activityConfiguration;

    @JsonProperty("cluster")
    private ClusterConfiguration clusterConfiguration;

    @JsonProperty("portal_server")
    private PortalServerConfiguration portalServerConfiguration;

    @JsonProperty("rate_limiting")
    private RateLimitingConfiguration rateLimitingConfiguration;

    @JsonProperty("rest")
    private RestConfiguration restConfiguration;

    @JsonProperty("session")
    private SessionConfiguration sessionConfiguration;

    @JsonProperty("redirect")
    private RedirectConfiguration redirectConfiguration;

    public boolean isEnableCluster() {
        return enableCluster;
    }

    public void setEnableCluster(boolean enableCluster) {
        this.enableCluster = enableCluster;
    }

    public boolean isEnableRateLimiting() {
        return enableRateLimiting;
    }

    public boolean isCheckRedirectUrl() {
        return checkRedirectUrl;
    }

    public void setCheckRedirectUrl(boolean checkRedirectUrl) {
        this.checkRedirectUrl = checkRedirectUrl;
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

    public RedirectConfiguration getRedirectConfiguration() {
        return redirectConfiguration;
    }

    public void setRedirectConfiguration(RedirectConfiguration redirectConfiguration) {
        this.redirectConfiguration = redirectConfiguration;
    }

    public String getDefaultAdminPassword() {
        return defaultAdminPassword;
    }

    public void setDefaultAdminPassword(String defaultAdminPassword) {
        this.defaultAdminPassword = defaultAdminPassword;
    }

    public String getDefaultAdminUsername() {
        return defaultAdminUsername;
    }

    public void setDefaultAdminUsername(String defaultAdminUsername) {
        this.defaultAdminUsername = defaultAdminUsername;
    }

    @Override
    public String toString() {
        return "ServerConfiguration{" +
                "privateKey='" + privateKey + '\'' +
                ", mainPageRedirectUrl='" + mainPageRedirectUrl + '\'' +
                ", allowNat=" + allowNat +
                ", enableCluster=" + enableCluster +
                ", enableRateLimiting=" + enableRateLimiting +
                ", checkRedirectUrl=" + checkRedirectUrl +
                ", defaultAdminUsername='" + defaultAdminUsername + '\'' +
                ", defaultAdminPasswordMd5='" + defaultAdminPassword + '\'' +
                ", activityConfiguration=" + activityConfiguration +
                ", clusterConfiguration=" + clusterConfiguration +
                ", portalServerConfiguration=" + portalServerConfiguration +
                ", rateLimitingConfiguration=" + rateLimitingConfiguration +
                ", restConfiguration=" + restConfiguration +
                ", sessionConfiguration=" + sessionConfiguration +
                ", redirectConfiguration=" + redirectConfiguration +
                '}';
    }
}
