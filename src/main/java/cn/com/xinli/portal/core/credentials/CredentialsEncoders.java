package cn.com.xinli.portal.core.credentials;

import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.persistence.DiscriminatorValue;
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
    @DiscriminatorValue("NO-OP")
    public static class NoOpEncoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            Credentials populate = new DefaultCredentials();
            populate.setUsername(credentials.getUsername());
            populate.setPassword(credentials.getPassword());
            populate.setIp(credentials.getIp());
            populate.setMac(credentials.getMac());
            return populate;
        }
    }

    /**
     * MD5 encoder.
     * <p>Encode password in {@link Credentials} with MD5 summary.
     */
    @Entity
    @DiscriminatorValue("MD5")
    public static class Md5Encoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            Credentials populate = new DefaultCredentials();
            populate.setUsername(credentials.getUsername());
            populate.setPassword(Hex.encodeHexString(CodecUtils.md5sum(credentials.getPassword().getBytes())));
            populate.setIp(credentials.getIp());
            populate.setMac(credentials.getMac());
            return populate;
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * <p>Encode password in {@link Credentials} with HMAC-SHA1 Hex string.
     */
    @Entity
    @DiscriminatorValue("SHA1-HEX")
    public static class Sha1HexEncoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            byte[] digest = CodecUtils.hmacSha1(credentials.getPassword().getBytes(), additional);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encode credentials with HMAC-SHA1.");
            }

            Credentials populate = new DefaultCredentials();
            populate.setUsername(credentials.getUsername());
            populate.setPassword(Hex.encodeHexString(digest));
            populate.setIp(credentials.getIp());
            populate.setMac(credentials.getMac());
            return populate;
        }
    }

    /**
     * HMAC-SHA1 encoder.
     * <p>Encode password in {@link Credentials} with HMAC-SHA1 Base64 string.
     */
    @Entity
    @DiscriminatorValue("SHA1-BASE64")
    public static class Sha1Base64Encoder extends CredentialsEncoder {
        @Override
        public Credentials encode(Credentials credentials, String additional) {
            byte[] digest = CodecUtils.hmacSha1(credentials.getPassword().getBytes(), additional);
            if (digest.length < 1) {
                throw new RuntimeException("Failed to encode credentials with HMAC-SHA1.");
            }

            Credentials populate = new DefaultCredentials();
            populate.setUsername(credentials.getUsername());
            populate.setPassword(Base64.encodeBase64String(digest));
            populate.setIp(credentials.getIp());
            populate.setMac(credentials.getMac());
            return populate;
        }
    }
}
