package cn.com.xinli.portal.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

/**
 * Portal context.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Context {
    /** ip address. */
    @JsonProperty
    private String ip;

    /** Mac address. */
    @JsonProperty
    private String mac;

    /** nas ip. */
    @JsonProperty("nas_ip")
    private String nasIp;

    /** Extended information. */
    @JsonProperty("extended_information")
    private String extendedInformation;

    /** Session id. */
    @JsonProperty("session")
    private String session;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNasIp() {
        return nasIp;
    }

    public void setNasIp(String nasIp) {
        this.nasIp = nasIp;
    }

    public String getExtendedInformation() {
        return extendedInformation;
    }

    public void setExtendedInformation(String extendedInformation) {
        this.extendedInformation = extendedInformation;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    /**
     * Check if context is valid.
     * @return true if valid.
     */
    @JsonIgnore
    public boolean isValid() {
        return !StringUtils.isEmpty(ip);
    }

    @Override
    public String toString() {
        return "Context{" +
                "session='" + session + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", nasIp='" + nasIp + '\'' +
                ", extendedInformation='" + extendedInformation + '\'' +
                '}';
    }
}
