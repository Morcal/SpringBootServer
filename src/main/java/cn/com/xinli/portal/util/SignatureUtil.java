package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Constants;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Signature utility.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/6.
 */
public class SignatureUtil {
    /**
     * Find algorithm.
     * @param algorithm algorithm name to match.
     * @return algorithm name if a match found, or default.
     */
    private static String findAlgorithm(String algorithm) {
        if (algorithm.toUpperCase().matches("HMAC.SHA1")) {
            return Constants.HMAC_SHA1;
        }
        return Constants.DEFAULT_KEY_SPEC;
    }

    /**
     * Calculate signature.
     *
     * @param data data.
     * @param key signing key.
     * @param hmacAlgorithm HMAC algorithm name.
     *                      default {@link Constants#DEFAULT_KEY_SPEC}
     * @return BASE64 encoded signature.
     */
    public static String sign(byte[] data, String key, String hmacAlgorithm) {
        String algorithm = StringUtils.isEmpty(hmacAlgorithm) ?
                Constants.DEFAULT_KEY_SPEC : findAlgorithm(hmacAlgorithm);

        String digest = null;
        try {
            byte[] fin = hmac(data, key, algorithm);
            digest = new String(Hex.encodeHex(fin));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return digest;
    }

    /**
     * Calculate HMAC using given key and algorithm from data.
     * @param data data.
     * @param key signing key.
     * @param algorithm hash algorithm name.
     * @return hmac bytes.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static byte[] hmac(byte[] data, String key, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(signingKey);
        return mac.doFinal(data);
    }
}
