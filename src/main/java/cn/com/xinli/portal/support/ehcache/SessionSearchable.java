package cn.com.xinli.portal.support.ehcache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Session Searchable to support searching in session cache.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/3.
 */
public class SessionSearchable implements EhcacheSearchable<Long> {
    private String username;
    private String ip;
    private String mac;
    private String nas;
    private Long value;

    public SessionSearchable username(String username) {
        this.username = username;
        return this;
    }

    public SessionSearchable ip(String ip) {
        this.ip = ip;
        return this;
    }

    public SessionSearchable mac(String mac) {
        this.mac = mac;
        return this;
    }

    public SessionSearchable nas(String nas) {
        this.nas = nas;
        return this;
    }

    public SessionSearchable value(Long value) {
        this.value = value;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getNas() {
        return nas;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }

    @Override
    public Collection<EhcacheSearchAttribute> getSearchAttributes() {
        List<EhcacheSearchAttribute> attributes = new ArrayList<>();
        attributes.add(EhcacheManagerAdapter.search("ip", String.class, "value.getIp()"));
        attributes.add(EhcacheManagerAdapter.search("mac", String.class, "value.getMac()"));
        attributes.add(EhcacheManagerAdapter.search("nas", String.class, "value.getNas()"));
        attributes.add(EhcacheManagerAdapter.search("username", String.class, "value.getUsername()"));
        return attributes;
    }
}
