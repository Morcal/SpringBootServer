package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class AddressUtil {
    /**
     * Validate incoming request ip.
     *
     * If realIp exists, then nginx detected.
     * @param realIp nginx header real ip.
     * @param sourceIp source ip in parameters.
     * @param request HTTP request.
     * @return true valid.
     */
    public static boolean validateIp(String realIp, String sourceIp, HttpServletRequest request) {
        String remote = request.getRemoteAddr();
        return StringUtils.isEmpty(realIp) ?
                StringUtils.equals(remote, sourceIp) :
                StringUtils.equals(realIp, sourceIp);
    }

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
}
