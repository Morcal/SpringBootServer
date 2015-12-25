package cn.com.xinli.portal;

/**
 * PWS server configuration.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public class ServerConfig {
    private String application;
    private String privateKey;
    private boolean requiresKeepalive;
    private int keepaliveInterval;
    private String derbyScheme;
    private boolean useDerbyMemDb;
    private String initSql;
    private String restSchemeScheme;
    private String restSchemeServer;
    private String restSchemeHost;
    private int restSchemePort;
    private String restSchemeVersion;
    private String activityLoggingSeverity;
    private int portalServerListenPort;
    private int portalServerThreadSize;
    private String portalServerSharedSecret;
    private String portalServerHuaweiVersion;
    private String serverIpAddress;

    public boolean useDerbyMemDb() {
        return useDerbyMemDb;
    }

    public void setUseDerbyMemDb(boolean useDerbyMemDb) {
        this.useDerbyMemDb = useDerbyMemDb;
    }

    public String getDerbyScheme() {
        return derbyScheme;
    }

    public void setDerbyScheme(String derbyScheme) {
        this.derbyScheme = derbyScheme.trim();
    }

    public String getInitSql() {
        return initSql;
    }

    public void setInitSql(String initSql) {
        this.initSql = initSql;
    }

    public boolean requiresKeepalive() {
        return requiresKeepalive;
    }

    public void setRequiresKeepalive(boolean requiresKeepalive) {
        this.requiresKeepalive = requiresKeepalive;
    }

    public int getKeepaliveInterval() {
        return keepaliveInterval;
    }

    public void setKeepaliveInterval(int keepaliveInterval) {
        this.keepaliveInterval = keepaliveInterval;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getActivityLoggingSeverity() {
        return activityLoggingSeverity;
    }

    public void setActivityLoggingSeverity(String activityLoggingSeverity) {
        this.activityLoggingSeverity = activityLoggingSeverity;
    }

    public String getPortalServerHuaweiVersion() {
        return portalServerHuaweiVersion;
    }

    public void setPortalServerHuaweiVersion(String portalServerHuaweiVersion) {
        this.portalServerHuaweiVersion = portalServerHuaweiVersion;
    }

    public int getPortalServerListenPort() {
        return portalServerListenPort;
    }

    public void setPortalServerListenPort(int portalServerListenPort) {
        this.portalServerListenPort = portalServerListenPort;
    }

    public String getPortalServerSharedSecret() {
        return portalServerSharedSecret;
    }

    public void setPortalServerSharedSecret(String portalServerSharedSecret) {
        this.portalServerSharedSecret = portalServerSharedSecret;
    }

    public int getPortalServerThreadSize() {
        return portalServerThreadSize;
    }

    public void setPortalServerThreadSize(int portalServerThreadSize) {
        this.portalServerThreadSize = portalServerThreadSize;
    }

    public boolean isRequiresKeepalive() {
        return requiresKeepalive;
    }

    public boolean isUseDerbyMemDb() {
        return useDerbyMemDb;
    }

    public String getRestSchemeHost() {
        return restSchemeHost;
    }

    public void setRestSchemeHost(String restSchemeHost) {
        this.restSchemeHost = restSchemeHost;
    }

    public int getRestSchemePort() {
        return restSchemePort;
    }

    public void setRestSchemePort(int restSchemePort) {
        this.restSchemePort = restSchemePort;
    }

    public String getRestSchemeScheme() {
        return restSchemeScheme;
    }

    public void setRestSchemeScheme(String restSchemeScheme) {
        this.restSchemeScheme = restSchemeScheme;
    }

    public String getRestSchemeServer() {
        return restSchemeServer;
    }

    public void setRestSchemeServer(String restSchemeServer) {
        this.restSchemeServer = restSchemeServer;
    }

    public String getRestSchemeVersion() {
        return restSchemeVersion;
    }

    public void setRestSchemeVersion(String restSchemeVersion) {
        this.restSchemeVersion = restSchemeVersion;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "activityLoggingSeverity='" + activityLoggingSeverity + '\'' +
                ", application='" + application + '\'' +
                ", privateKey='" + privateKey + '\'' +
                ", requiresKeepalive=" + requiresKeepalive +
                ", keepaliveInterval=" + keepaliveInterval +
                ", derbyScheme='" + derbyScheme + '\'' +
                ", useDerbyMemDb=" + useDerbyMemDb +
                ", initSql='" + initSql + '\'' +
                ", restSchemeScheme='" + restSchemeScheme + '\'' +
                ", restSchemeServer='" + restSchemeServer + '\'' +
                ", restSchemeHost='" + restSchemeHost + '\'' +
                ", restSchemePort=" + restSchemePort +
                ", restSchemeVersion='" + restSchemeVersion + '\'' +
                ", portalServerListenPort=" + portalServerListenPort +
                ", portalServerThreadSize=" + portalServerThreadSize +
                ", portalServerSharedSecret='" + portalServerSharedSecret + '\'' +
                ", portalServerHuaweiVersion=" + portalServerHuaweiVersion +
                ", serverIpAddress='" + serverIpAddress + '\'' +
                '}';
    }
}
