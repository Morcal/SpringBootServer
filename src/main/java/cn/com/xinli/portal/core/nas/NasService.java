package cn.com.xinli.portal.core.nas;

import java.util.stream.Stream;

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
public interface NasService {
    /**
     * Initialize NAS service.
     *
     * <p>Load NAS/BRAS devices in this method.
     */
    void init();

    /**
     * Reload NAS/BRAS devices.
     */
    void reload();

    /**
     * Get NAS device by id.
     * @param id nas id.
     * @return nas.
     * @throws NasNotFoundException
     */
    Nas get(Long id) throws NasNotFoundException;

    Nas find(String name) throws NasNotFoundException;

    Stream<Nas> all();
}
