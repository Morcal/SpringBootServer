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
    Date getStartDate();

    /**
     * Get session id.
     * @return session id.
     */
    long getId();

    String getIp();

    String getMac();

    static String pair(String ip, String mac) {
        return (ip + " " + (StringUtils.isEmpty(mac) ? "unknown" : mac)).trim();
    }
}
