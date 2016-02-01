package cn.com.xinli.portal.core;

import cn.com.xinli.portal.core.nas.NasNotFoundException;

/**
 * Route entry mapper.
 *
 * <p>Classes implement this interface save routing entries in a map,
 * so routing entries can be retrieved later.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface RouteMapper {
    /**
     * Map user/client to their originate NAS/BRAS device by device's ip address.
     * @param ip user ip.
     * @param mac user mac.
     * @param nasIp NAS/BRAS device ip address.
     * @throws NasNotFoundException
     */
    void map(String ip, String mac, String nasIp) throws NasNotFoundException;
}
