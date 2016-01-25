package cn.com.xinli.portal.service;

import cn.com.xinli.portal.core.*;

/**
 * Nas Mapping.
 *
 * <p>This class contains NAS/BRAS and clients mappings.
 *
 * <p>When client/user access PWS entry for the first time,
 * PWS records client/user's information and the {@link Nas} information
 * by creating a ip/mac to NAS configuration mapping.
 *
 * <p>PWS uses this mapping to handle client/user's portal
 * login/logout requests, and will communicate with mapped
 * {@link Nas}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
public interface NasService extends NasLocator, NasContainer {
    /**
     * Reload NAS/BRAS devices.
     */
    void reload();

    /**
     * Map user/client to their originate NAS/BRAS device by device's ip address.
     * @param userKey user/client key.
     * @param nasKey NAS/BRAS device ip address.
     * @throws NasNotFoundException
     */
    void map(String userKey, String nasKey) throws NasNotFoundException;
}
