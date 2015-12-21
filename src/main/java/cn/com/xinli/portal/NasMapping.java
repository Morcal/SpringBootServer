package cn.com.xinli.portal;

/**
 * Nas Mapping.
 *
 * <p>This class contains NAS/BRAS and clients mappings.</p>
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface NasMapping {
    /**
     * Reload.
     */
    void reload();

    /**
     * Create a mapping from user ip and mac to NAS device configuration.
     * @param userIp user ip.
     * @param userMac user mac.
     * @param nasIp NAS ip.
     * @throws PortalException
     */
    void map(String userIp, String userMac, String nasIp) throws PortalException;

    /**
     * Find NAS for given user ip and user mac.
     * @param userIp user ip address.
     * @param userMac user mac address.
     * @return NAS matches ip and mac, or null if not found.
     */
    Nas findNas(String userIp, String userMac);

    /**
     * Find nas by ipv4 address range.
     * @param ip ip v4 address in {@link Integer} form.
     * @return nas found if matches or null.
     */
    Nas findByIpv4Range(int ip);
}
