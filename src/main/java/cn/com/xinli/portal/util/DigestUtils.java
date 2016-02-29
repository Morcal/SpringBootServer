package cn.com.xinli.portal.util;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.ServerException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Digest utilities.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/25.
 */
public class DigestUtils {
    public enum SupportedAlgorithm {
        SHA256("SHA-256"),
        SHA512("SHA-512");

        private final String algorithm;

        SupportedAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getAlgorithm() {
            return algorithm;
        }
    }
    /**
     * Get message digest.
     * @param algorithm digest algorithm.
     * @return message digest.
     * @throws ServerException
     */
    public static MessageDigest getMessageDigest(String algorithm) throws ServerException {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new ServerException(
                    PortalError.SERVER_INTERNAL_ERROR,
                    "failed to create message digest",
                    e);
        }
    }

    public static String shaHex(SupportedAlgorithm algorithm, byte[] value) throws ServerException {
        Objects.requireNonNull(algorithm);
        if (value == null || value.length < 1) {
            throw new IllegalArgumentException("context can not be empty.");
        }

        final byte[] digest = getMessageDigest(algorithm.getAlgorithm()).digest(value);
        return Hex.encodeHexString(digest);
    }

    public static String sha512Hex(byte[] value) throws ServerException {
        return shaHex(SupportedAlgorithm.SHA512, value);
    }

    public static String sha256Hex(byte[] value) throws ServerException {
        return shaHex(SupportedAlgorithm.SHA256, value);
    }

    public static String sha512Hex(String value) throws ServerException {
        Objects.requireNonNull(value);
        return shaHex(SupportedAlgorithm.SHA512, value.getBytes());
    }

    public static String sha256Hex(String value) throws ServerException {
        Objects.requireNonNull(value);
        return shaHex(SupportedAlgorithm.SHA256, value.getBytes());
    }

}
