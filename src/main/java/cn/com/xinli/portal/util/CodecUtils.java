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
import java.util.stream.Stream;

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

    private static class Escape {
        String expression;
        char value;

        static Escape of(String expression, char value) {
            Escape escape = new Escape();
            escape.expression = expression;
            escape.value = value;
            return escape;
        }
    }

    private static final Escape[] PIN_PREFIX_REGEX = {
            Escape.of("\\t", '\t'),
            Escape.of("\\b", '\b'),
            Escape.of("\\n", '\n'),
            Escape.of("\\r", '\r'),
            Escape.of("\\f", '\f'),
    };

    /**
     * Unescape string.
     * @param text text.
     * @return unescaped string.
     */
    public static String unescapeString(String text) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\\' && i < text.length() - 1) {
                for (Escape e : PIN_PREFIX_REGEX) {
                    if (text.substring(i, i + 2).equals(e.expression)) {
                        builder.append(e.value);
                    }
                }
                i++;
            } else {
                builder.append(text.charAt(i));
            }
        }

        return builder.toString();
    }
}
