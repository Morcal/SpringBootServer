package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Portal session rest bean.
 *
 * <p>Project: xpws.
 *
 * @author zhoupeng 2015/12/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionBean {
    /** Session id. */
    @JsonProperty
    private String id;

    /** Session token. */
    @JsonProperty
    private String token;

    /** Token expires in seconds. */
    @JsonProperty("token_expires_in")
    private long tokenExpiresIn;

    /** If server requires client to keep alive. */
    @JsonProperty("keepalive")
    private boolean keepAlive;

    /** UNIX epoch time this session started at. */
    @JsonProperty("started_at")
    private long startTime; // This field is redundant if accounting info provided.

    /** Server suggested keep alive interval in seconds. */
    @JsonProperty("keepalive_interval")
    private int keepAliveInterval;

    /** Session associated context. */
    @JsonProperty
    private String context;

    @JsonProperty("authentication")
    private Object authentication;

    @JsonProperty("authorization")
    private Object authorization;

    @JsonProperty("accounting")
    private Object accounting;

    /** If network changed. */
    @JsonProperty("network_changed")
    private boolean networkChanged = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenExpiresIn() {
        return tokenExpiresIn;
    }

    public void setTokenExpiresIn(long tokenExpiresIn) {
        this.tokenExpiresIn = tokenExpiresIn;
    }

    public Object getAccounting() {
        return accounting;
    }

    public void setAccounting(Object accounting) {
        this.accounting = accounting;
    }

    public Object getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Object authentication) {
        this.authentication = authentication;
    }

    public Object getAuthorization() {
        return authorization;
    }

    public void setAuthorization(Object authorization) {
        this.authorization = authorization;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isNetworkChanged() {
        return networkChanged;
    }

    public void setNetworkChanged(boolean networkChanged) {
        this.networkChanged = networkChanged;
    }

    @Override
    public String toString() {
        return "SessionBean{" +
                "id='" + id + '\'' +
                ", token='" + token + '\'' +
                ", tokenExpiresIn=" + tokenExpiresIn +
                ", keepAlive=" + keepAlive +
                ", startTime=" + startTime +
                ", keepAliveInterval=" + keepAliveInterval +
                ", context='" + context + '\'' +
                ", networkChanged=" + networkChanged +
                ", authentication=" + authentication +
                ", authorization=" + authorization +
                ", accounting=" + accounting +
                '}';
    }
}
