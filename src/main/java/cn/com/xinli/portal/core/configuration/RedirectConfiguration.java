package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Redirect Configuration.
 *
 * <p>Server needs to understand portal web redirect url for varies of
 * NAS/BRAS. This configuration contains redirect parameters mappings
 * so server can translate incoming redirect url to known parameters.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RedirectConfiguration {
    /** Supported Redirect user ip parameter names. */
    @JsonProperty("user_ip_aliases")
    private String[] userIp;

    /** Supported Redirect user mac parameter names. */
    @JsonProperty("user_mac_aliases")
    private String[] userMac;

    /** Supported Redirect nas ip parameter names. */
    @JsonProperty("nas_ip_aliases")
    private String[] nasIp;

    /**
     * Get supported user ip redirect parameter names.
     * @return user ip redirect parameter names.
     */
    public String[] getUserIp() {
        return userIp;
    }

    public void setUserIp(String[] userIp) {
        this.userIp = userIp;
    }

    /**
     * Get supported user mac redirect parameter names.
     * @return user mac redirect parameter names.
     */
    public String[] getUserMac() {
        return userMac;
    }

    public void setUserMac(String[] userMac) {
        this.userMac = userMac;
    }

    /**
     * Get supported nas ip redirect parameter names.
     * @return nas ip redirect parameter names.
     */
    public String[] getNasIp() {
        return nasIp;
    }

    public void setNasIp(String[] nasIp) {
        this.nasIp = nasIp;
    }

    @Override
    public String toString() {
        return "RedirectConfiguration{" +
                "userIp=" + Arrays.toString(userIp) +
                ", userMac=" + Arrays.toString(userMac) +
                ", nasIp=" + Arrays.toString(nasIp) +
                '}';
    }
}
