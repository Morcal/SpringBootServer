package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringJoiner;

/**
 * Address utility.
 *
 * <p>Project: xpws
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
     * Format mac address in format "xx:xx:xx:xx:xx:xx".
     * @param mac original mac.
     * @return formatted mac.
     */
    public static String formatMac(String mac) {
        if (StringUtils.isEmpty(mac)) {
            throw new IllegalArgumentException("mac can not be blank");
        }
        final String value = mac.replace(":", "").replace("-", "").toLowerCase().trim();
        if (StringUtils.isEmpty(value) || value.length() != 12) {
            throw new IllegalArgumentException("given value is not a valid mac.");
        }

        StringJoiner joiner = new StringJoiner(":");
        for (int i = 0; i < 12; i = i + 2) {
            joiner.add(value.substring(i, i + 2));
        }

        return joiner.toString();
    }
}
