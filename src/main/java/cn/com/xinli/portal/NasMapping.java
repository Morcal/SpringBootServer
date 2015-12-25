package cn.com.xinli.portal;

import java.util.Optional;

/**
 * Nas Mapping.
 * <p>
 * This class contains NAS/BRAS and clients mappings.
 * <p>
 * When client/user access PWS entry for the first time,
 * PWS records client/user's information and the {@link Nas} information
 * by creating a ip/mac to NAS configuration mapping.
 * <p>
 * PWS uses this mapping to handle client/user's portal
 * login/logout requests, and will communicate with mapped
 * {@link Nas}.
 * <p>
 * Project: xpws.
 *
 * @author zhoupeng 2015/12/2.
 */
public interface NasMapping {
    /**
     * Reload.
     */
    void reload();

    /**
     * Get NAS/BRAS by id.
     *
     * @param id NAS/BRAS id.
     * @return NAS if found or null.
     */
    Optional<Nas> getNas(long id);

    /**
     * Get NAS/BRAS by nas id.
     * @param nasId NAS id.
     * @return {@link Nas} if found or null.
     * @see Nas#getNasId()
     */
    Optional<Nas> getNasByNasId(String nasId);

    /**
     * Create a mapping from user ip and mac to NAS device configuration.
     *
     * @param userIp  user ip.
     * @param userMac user mac.
     * @param nasIp   NAS ip.
     * @throws PortalException
     */
    void map(String userIp, String userMac, String nasIp) throws PortalException;

    /**
     * Find NAS for given user ip and user mac.
     *
     * @param userIp  user ip address.
     * @param userMac user mac address.
     * @return NAS matches ip and mac, or null if not found.
     */
    Nas findNas(String userIp, String userMac);

    /**
     * Find nas by ipv4 address range.
     *
     * @param ip ip v4 address in {@link Integer} form.
     * @return nas found if matches or null.
     */
    Nas findByIpv4Range(int ip);
}
