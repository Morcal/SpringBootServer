package cn.com.xinli.portal.core.configuration;

/**
 * Session configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class SessionConfiguration {
    private boolean enableTtl;
    private int ttl;
    private boolean enableHeartbeat;
    private int heartbeatInterval;
    private int minUpdateInterval;
    private int tokenTtl;

    public boolean isEnableTtl() {
        return enableTtl;
    }

    public void setEnableTtl(boolean enableTtl) {
        this.enableTtl = enableTtl;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public boolean isEnableHeartbeat() {
        return enableHeartbeat;
    }

    public void setEnableHeartbeat(boolean enableHeartbeat) {
        this.enableHeartbeat = enableHeartbeat;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public int getMinUpdateInterval() {
        return minUpdateInterval;
    }

    public void setMinUpdateInterval(int minUpdateInterval) {
        this.minUpdateInterval = minUpdateInterval;
    }

    public int getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(int tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    @Override
    public String toString() {
        return "SessionConfiguration{" +
                "enableTtl=" + enableTtl +
                ", ttl=" + ttl +
                ", tokenTtl=" + tokenTtl +
                ", enableHeartbeat=" + enableHeartbeat +
                ", heartbeatInterval=" + heartbeatInterval +
                ", minUpdateInterval=" + minUpdateInterval +
                '}';
    }
}
