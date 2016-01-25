package cn.com.xinli.portal.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Cache entity for session.
 *
 * <p>It's unnecessary to save all details of {@link Session} in cache
 * especially in distributed system. So server should translate originate
 * session to a cache-friendly-and-human-readable form, like JSON object.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CachedSession implements Session, Cacheable {
    @JsonProperty
    private long id;

    @JsonProperty
    String nas;

    @JsonProperty
    String username;

    @JsonProperty
    String ip;

    @JsonProperty
    String mac;

    @JsonProperty(value = "start_time")
    Date startTime;

    @JsonProperty(value = "last_modified")
    long lastModified;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNas() {
        return nas;
    }

    public void setNas(String nas) {
        this.nas = nas;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String getAppName() {
        return null;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    @Override
    public String getNasName() {
        return nas;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Create a cache entity for session.
     * @param session session to cache.
     * @return cache entity.
     */
    public static CachedSession from(DbSession session) {
        CachedSession value = new CachedSession();
        value.id = session.getId();
        value.nas = session.getNas().getName();
        value.ip = session.getIp();
        value.mac = session.getMac();
        value.username = session.getUsername();
        value.startTime = session.getStartTime();
        value.lastModified = System.currentTimeMillis();
        return value;
    }

    @Override
    public String toString() {
        return "CachedSession{" +
                "id=" + id +
                ", nas=" + nas +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }

    @Override
    public Object getKey() {
        return id;
    }
}
