package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * PWS portal session.
 * <p>
 * This class instances represent portal authentication based
 * broadband internet connections.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Session {
    /**
     * Get Session start date.
     *
     * @return session start date.
     */
    Date getStartTime();

    /**
     * Get session end date.
     * @return session end date.
     */
    Date getEndTime();

    /**
     * Get session id.
     *
     * @return session id.
     */
    long getId();

    /**
     * Get session user ip address.
     * <p>Normally, user's ip address will never be null.</p>
     * @return session user ip address.
     */
    String getIp();

    /**
     * Get session user mac address if presents.
     * @return user's mac or null.
     */
    String getMac();

    /**
     * Get {@link Nas} id.
     * @return nas id.
     */
    long getNasId();

    /**
     * Get session user name.
     * @return session user name.
     */
    String getUsername();

    /**
     * Get session user password.
     * @return session user password.
     */
    String getPassword();

    /**
     * Get session user device.
     * @return session suer device.
     */
    String getDevice();

    /**
     * Create a paired information for ip and mac.
     * <p>
     * If mac is missing, "unknown" will be used.
     *
     * @param ip  ip address.
     * @param mac mac address.
     * @return paired information.
     */
    static String pair(String ip, String mac) {
        return (ip + " " + (StringUtils.isEmpty(mac) ? "unknown" : mac)).trim();
    }
}
