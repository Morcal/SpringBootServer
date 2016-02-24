package cn.com.xinli.portal.transport;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Transport address utilities.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public class TransportUtils {
    /**
     * Get ipv4 address in bytes.
     *
     * <p>FIXME: can't apply on ipv6 address.
     *
     * @param ip ipv4 address in form of "xxx.xxx.xxx.xxx".
     * @return ipv4 address in bytes.
     * @throws UnknownHostException
     */
    public static byte[] getIp4Address(String ip) throws IOException {
        byte[] bytes = InetAddress.getByName(ip).getAddress();
        if (bytes.length > 4) {
            return Arrays.copyOfRange(bytes, bytes.length - 4, bytes.length);
        } else {
            return Arrays.copyOf(bytes, 4);
        }
    }

    /**
     * Get ipv4 address in string.
     * @param address address.
     * @return ipv4 string.
     * @throws UnknownHostException
     */
    public static String getIp4Address(byte[] address) throws UnknownHostException {
        return InetAddress.getByAddress(address).getHostAddress();
    }

    /**
     * Convert mac address to bytes.
     * @param mac mac address.
     * @return bytes.
     */
    public static byte[] convertMac(String mac) {
        if (StringUtils.isEmpty(mac)) {
            throw new IllegalArgumentException("mac can not be blank");
        }

        final String value = mac.replace(":", "").replace("-", "").toLowerCase().trim();
        if (StringUtils.isEmpty(value) || value.length() != 12) {
            throw new IllegalArgumentException("given value is not a valid mac.");
        }

        try {
            return Hex.decodeHex(value.toCharArray());
        } catch (DecoderException e) {
            return new byte[0];
        }
    }

    /**
     * Convert bytes to hex string.
     * @param bytes input bytes.
     * @return hex string.
     */
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        String hex = Hex.encodeHexString(bytes);
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < hex.length(); i = i + 2) {
            joiner.add(hex.substring(i, i + 2));
        }

        return joiner.toString();
    }
}
