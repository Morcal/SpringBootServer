package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Session {
    /**
     * Get Session start date.
     * @return session start date.
     */
    Date getStartTime();

    Date getEndTime();

    /**
     * Get session id.
     * @return session id.
     */
    long getId();

    String getIp();

    String getMac();

    long getNasId();

    /**
     * Create a paired information for ip and mac.
     *
     * <p>If mac is missing, "unknown" will be used.</p>
     * @param ip ip address.
     * @param mac mac address.
     * @return paired information.
     */
    static String pair(String ip, String mac) {
        return (ip + " " + (StringUtils.isEmpty(mac) ? "unknown" : mac)).trim();
    }

    String getUsername();

    String getPassword();
}
