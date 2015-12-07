package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class SignatureUtil {

    /**
     * Calculate signature.
     *
     * @param data data.
     * @param key signing key.
     * @param hmacAlgorithm HMAC algorithm name.
     *                      default {@Link Constant#DEFAULT_KEY_SPEC}
     * @return BASE64 encoded signature.
     */
    public static String sign(byte[] data, String key, String hmacAlgorithm) {
        String algorithm = StringUtils.isEmpty(hmacAlgorithm) ?
                Constants.DEFAULT_KEY_SPEC : hmacAlgorithm;

        String digest = null;
        try {
            byte[] fin = hmac(data, key, algorithm);
            digest = Base64Utils.encodeToString(fin);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return digest;
    }

    /**
     * Calculate HMAC using given key and algorithm from data.
     * @param data data.
     * @param key signing key.
     * @param algorithm algorithm name.
     * @return hmac bytes.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static byte[] hmac(@NotNull byte[] data, String key, String algorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(signingKey);
        return mac.doFinal(data);
    }
}
