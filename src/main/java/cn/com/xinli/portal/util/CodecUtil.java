package cn.com.xinli.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Codec utility.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public class CodecUtil {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CodecUtil.class);

    /** HMAC-SHA1 algorithm name. */
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

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
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
