package cn.com.xinli.portal.core;

/**
 * NAS manager.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
public interface NasManager {
    /**
     * Create a NAS configuration.
     * @param name nas name.
     * @param ipv4Address IPv4 address.
     * @param ipv6Address IPv6 address.
     * @param type nas type.
     * @param listenPort listen port.
     * @param authType authentication type.
     * @param sharedSecret shared secret key.
     * @param translation translation, can be null.
     * @return NAS.
     */
    Nas createNas(String name,
                  String ipv4Address,
                  String ipv6Address,
                  NasType type,
                  int listenPort,
                  AuthType authType,
                  String sharedSecret,
                  CredentialsTranslation translation);

    /**
     * Delete a NAS configuration.
     *
     * @param id NAS id to delete.
     */
    void deleteNas(long id);

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
}
