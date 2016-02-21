package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
public class AddressUtil {
    /**
     * Convert ipv4 address to integer.
     * @param ip ipv4 address.
     * @return integer.
     * @throws IllegalArgumentException
     */
    public static int convertIpv4Address(String ip) {
        if (ip == null || ip.length() < 1 || ip.split("\\.").length != 4) {
            throw new IllegalArgumentException("invalid ipv4 address: " + ip);
        }

        int val = 0;
        try {
            InetAddress address = Inet4Address.getByName(ip);
            byte[] bytes = address.getAddress();
            for (byte aByte : bytes) {
                val <<= 8;
                val |= aByte & 0xFF;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return val;
    }

    /**
     * Trim mac address.
     * @param mac original mac.
     * @return trimmed mac.
     */
    public static String trimMac(String mac) {
        if (StringUtils.isEmpty(mac)) {
            throw new IllegalArgumentException("mac can not be blank");
        }

        return mac.replace(":", "").replace("-", "").toUpperCase();
    }
}
