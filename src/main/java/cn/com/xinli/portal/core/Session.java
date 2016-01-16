package cn.com.xinli.portal.core;

import cn.com.xinli.portal.protocol.Nas;
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
     * Get session last modified time (UNIX epoch time).
     * @return last modified time (UNIX epoch time).
     */
    long getLastModified();

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
     * Get session user operation system name.
     * @return session user operation system name.
     */
    String getOs();

    /**
     * Get session user client version.
     * @return user client version.
     */
    String getVersion();

    /**
     * Get session additional information.
     * @return session additional information.
     */
    default String getInfo()  {
        return getPassword() + ":" + getMac() + ":" + getOs() + ":" + getVersion();
    }

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
