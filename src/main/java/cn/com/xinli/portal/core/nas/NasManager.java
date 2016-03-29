package cn.com.xinli.portal.core.nas;

/**
 * NAS manager.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
public interface NasManager {
    /**
     * Create NAS/BRAS device.
     * @param nas nas.
     * @return nas.
     */
    Nas create(Nas nas);

    /**
     * Delete a NAS configuration.
     *
     * @param nas NAS id to delete.
     */
    void delete(Nas nas) throws NasNotFoundException;

    /**
     * Create NAS domain rule for NAS.
     * @param nas nas to create rule for.
     * @param domains domains
     * @return nas rule.
     */
    NasRule createNasDomainRule(Nas nas, String[] domains);

    /**
     * Create NAS IPv4 range rule for NAS.
     * @param nas nas to create rule for.
     * @param startIp start ip v4 address.
     * @param endIp end ip v4 address.
     * @return nas rule.
     */
    NasRule createNasIpv4RangeRule(Nas nas, String startIp, String endIp);


    /**
     * Create NAS/BRAS device for given name.
     *
     * <p>If HUAWEI NAS is enabled for developing purpose,
     * This method will create a HUAWEI NAS.
     *
     * @param nasConfig NAS/BRAS device config.
     * @return NAS.
     */
    Nas createHuaweiNas(NasConfig nasConfig);
}
