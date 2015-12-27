package cn.com.xinli.portal;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Device (NAS/BRAS) configuration.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public interface Nas {

    /**
     * Get id.
     * <p>This id is unique and working in scope of PWS only.</p>
     * @return id.
     */
    long getId();

    /**
     * Get nas id.
     * <p>Nas id may not be unique and may be same value
     * which exists in nas' configurations.
     * @return nas id.
     */
    String getNasId();

    /**
     * Get ipv4 address.
     * @return ipv4 address.
     */
    String getIpv4Address();

    /**
     * Get ipv6 address.
     * @return ipv6 address.
     */
    String getIpv6Address();

    /**
     * Get nas type string.
     * @return nas type string.
     */
    NasType getType();

    /**
     * Get nas portal listen port.
     * @return portal listen port.
     */
    int getListenPort();

    /**
     * Get authentication type.
     * @return authentication type.
     */
    AuthType getAuthType();

    /**
     * Get ipv4 end address if presents.
     * @return ipv4 end address in {@link Integer} form.
     */
    int getIpv4end();

    /**
     * Get ipv4 start address if presents.
     * @return ipv4 start address in {@link Integer} form.
     */
    int getIpv4start();

    /**
     * Get configured shared secret.
     * @return configured shared secret, may be empty but never null.
     */
    String getSharedSecret();

    /**
     * Get nas' ip address.
     * <p>By default, PWS will check nas' ipv4 address first,
     * if not presented, then check ipv6 address.</p>
     * @return ipv4 address if present, or ipv6 address, could be null.
     */
    default String getIp() {
        return StringUtils.isEmpty(getIpv4Address()) ? getIpv6Address() : getIpv4Address();
    }

    /**
     * Get nas' {@link InetAddress}.
     * @return nas' {@link InetAddress}.
     * @throws UnknownHostException
     */
    default InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(getIp());
    }
}
