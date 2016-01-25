package cn.com.xinli.portal.core;

import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.persistence.Entity;

/**
 * Credentials MD5 encoder.
 *
 * <p>This class provides default encoders for Credentials encoding.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/1.
 */
public class CredentialsEncoders {
    /**
     * No operation encoder.
     * <p>This encoder only copies original credentials without modifying.
     */
    @Entity
    public static class NoOpEncoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            return new Credentials(
                    credentials.getUsername(),
                    credentials.getPassword(),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * MD5 encoder.
     * <p>Encode password in {@link Credentials} with MD5 summary.
     */
    @Entity
    public static class Md5Encoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            return new Credentials(
                    credentials.getUsername(),
                    new String(Hex.encodeHex(CodecUtils.md5sum(credentials.getPassword().getBytes()))),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * <p>Encode password in {@link Credentials} with HMAC-SHA1 Hex string.
     */
    @Entity
    public static class Sha1HexEncoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            byte[] digest = CodecUtils.hmacSha1(credentials.getPassword().getBytes(), additional);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encode credentials with HMAC-SHA1.");
            }

            return new Credentials(
                    credentials.getUsername(),
                    new String(Hex.encodeHex(digest)),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * <p>Encode password in {@link Credentials} with HMAC-SHA1 Base64 string.
     */
    @Entity
    public static class Sha1Base64Encoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            byte[] digest = CodecUtils.hmacSha1(credentials.getPassword().getBytes(), additional);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encode credentials with HMAC-SHA1.");
            }

            return new Credentials(
                    credentials.getUsername(),
                    new String(Base64.encodeBase64(digest)),
                    credentials.getIp(),
                    credentials.getMac());
        }
    }
}
