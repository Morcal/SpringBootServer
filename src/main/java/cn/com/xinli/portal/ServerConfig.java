package cn.com.xinli.portal;

/**
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
    private String activityLoggingSeverity;

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
                '}';
    }
}
