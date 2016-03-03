package cn.com.xinli.portal.core.configuration;

/**
 * Session configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class SessionConfiguration {
    /** Server enabled session ttl. if enabled, session will be removed
     * when it reaches ttl.
     */
    private boolean enableTtl;

    /** Time to live. */
    private int ttl;

    /** Server enabled heart-beat. if true, no-heart-beat session will be removed
     * when certain time elapsed.  */
    private boolean enableHeartbeat;

    /** Server suggests heart-beat interval in seconds. */
    private int heartbeatInterval;

    /** Server allowed minimum update interval in seconds,
     * if incoming update interval less than this value, server will
     * respond an error to originate client.
     */
    private int minUpdateInterval;

    /** Session token time to live in seconds. */
    private int tokenTtl;

    /** Remove queue max length. */
    private int removeQueueMaxLength;

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

    public int getRemoveQueueMaxLength() {
        return removeQueueMaxLength;
    }

    public void setRemoveQueueMaxLength(int removeQueueMaxLength) {
        this.removeQueueMaxLength = removeQueueMaxLength;
    }

    @Override
    public String toString() {
        return "SessionConfiguration{" +
                "enableTtl=" + enableTtl +
                ", ttl=" + ttl +
                ", enableHeartbeat=" + enableHeartbeat +
                ", heartbeatInterval=" + heartbeatInterval +
                ", minUpdateInterval=" + minUpdateInterval +
                ", tokenTtl=" + tokenTtl +
                ", removeQueueMaxLength=" + removeQueueMaxLength +
                '}';
    }
}
