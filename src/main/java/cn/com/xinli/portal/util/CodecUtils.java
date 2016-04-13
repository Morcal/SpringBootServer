package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Constants;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Codec utility.
 *
 * <p>This class provides several functions for
 * <ul>
 *     <li>MD5 summary</li>
 *     <li>HMAC-SHA1</li>
 *     <li>ip address converting</li>
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public class CodecUtils {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CodecUtils.class);

    /**
     * Calculate MD5 summary.
     *
     * @param data data to calculate.
     * @return calculated bytes.
     */
    public static byte[] md5sum(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("md5 summary data can not be empty.");

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(data);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Missing MD5 algorithm.", e);
            return new byte[0];
        }
    }

    /**
     * Calculate HMAC-SHA1 summary.
     * @param data data to calculate.
     * @param key private key.
     * @return summary in bytes or byte array of length 0 if failed.
     */
    public static byte[] hmacSha1(byte[] data, String key) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), Constants.DEFAULT_KEY_SPEC);
            Mac mac = Mac.getInstance(Constants.DEFAULT_KEY_SPEC);
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Unescape string.
     * @param text text.
     * @return unescaped string.
     */
    public static String unescapeString(String text) {
        final char b = 'a';

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\\' && i < text.length() - 1) {
                char v = text.charAt(i + 1);
                builder.append((char) (v - b));
                i++;
            } else {
                builder.append(text.charAt(i));
            }
        }

        return builder.toString();
    }
}
