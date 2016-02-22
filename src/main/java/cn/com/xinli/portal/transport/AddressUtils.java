package cn.com.xinli.portal.transport;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.codec.binary.Hex;

/**
 * Transport address utilities.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public class AddressUtils {
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
}
