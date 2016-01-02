package cn.com.xinli.portal.util;

import cn.com.xinli.portal.Credentials;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;

/**
 * Credentials MD5 encoder.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public class CredentialsEncoders {

    public static CredentialsEncoder getEncoder(String name, String value) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Encoder's name can not be empty.");
        }

        switch (name) {
            case "no-op":
                return new NoOpEncoder();

            case "md5":
                return new Md5Encoder();

            case "sha1hex":
                return new Sha1HexEncoder(value);

            case "sha1base64":
                return new Sha1Base64Encoder(value);

            default:
                throw new IllegalArgumentException("encoder not supported.");
        }
    }

    /**
     * No operation encoder.
     */
    public static class NoOpEncoder implements CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials) {
            return new Credentials(
                    credentials.getUsername(),
                    credentials.getPassword(),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * MD5 encoder.
     * Encode password in {@link Credentials} with MD5 summary.
     */
    public static class Md5Encoder implements CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials) {
            return new Credentials(
                    credentials.getUsername(),
                    new String(Hex.encode(CodecUtil.md5sum(credentials.getPassword().getBytes()))),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * Encode password in {@link Credentials} with HMAC-SHA1 Hex string.
     */
    public static class Sha1HexEncoder implements CredentialsEncoder {
        private final String key;

        public Sha1HexEncoder(String key) {
            this.key = key;
        }

        @Override
        public Credentials encode(Credentials credentials) {
            byte[] digest = CodecUtil.hmacSha1(credentials.getPassword().getBytes(), key);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encodec credentials with HMAC-SHA1.");
            }

            return new Credentials(
                    credentials.getUsername(),
                    new String(Hex.encode(digest)),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * Encode password in {@link Credentials} with HMAC-SHA1 Base64 string.
     */
    public static class Sha1Base64Encoder implements CredentialsEncoder {
        private final String key;

        public Sha1Base64Encoder(String key) {
            this.key = key;
        }

        @Override
        public Credentials encode(Credentials credentials) {
            byte[] digest = CodecUtil.hmacSha1(credentials.getPassword().getBytes(), key);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encodec credentials with HMAC-SHA1.");
            }

            return new Credentials(
                    credentials.getUsername(),
                    new String(Base64.encode(digest)),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }
}
