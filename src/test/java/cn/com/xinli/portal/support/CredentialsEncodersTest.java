package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.CredentialsEncoder;
import cn.com.xinli.portal.core.credentials.CredentialsEncoders;
import cn.com.xinli.portal.core.credentials.DefaultCredentials;
import cn.com.xinli.portal.util.CodecUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
public class CredentialsEncodersTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CredentialsEncodersTest.class);

    final String secret = "s3cr3t";
    Credentials credentials;
    static final String USERNAME = "foo",
            PASSWORD = "bar",
            IP = "192.168.3.26",
            MAC = "20-CF-30-BB-E9-AF";

    @Before
    public void setup() {
        credentials = DefaultCredentials.of(USERNAME, PASSWORD, IP, MAC);
    }

    @Test
    public void testEncoders() throws UnsupportedEncodingException {
        final CredentialsEncoder noop = new CredentialsEncoders.NoOpEncoder(),
                md5 =  new CredentialsEncoders.Md5Encoder(),
                sha1hex =  new CredentialsEncoders.Sha1HexEncoder(),
                sha1base64 =  new CredentialsEncoders.Sha1Base64Encoder();

        Credentials encoded = noop.encode(credentials, secret);

        logger.debug("no-op encoded: {}", encoded);

        Assert.assertEquals(encoded, credentials);

        encoded = md5.encode(credentials, secret);

        logger.debug("md5 encoded: {}", encoded);
        String md5summary = Hex.encodeHexString(CodecUtils.md5sum(credentials.getPassword().getBytes()));

        Assert.assertEquals(md5summary, encoded.getPassword());

        byte[] sha1 = CodecUtils.hmacSha1(credentials.getPassword().getBytes(), secret);

        encoded = sha1hex.encode(credentials, secret);

        logger.debug("sha1 hex encoded: {}", encoded);

        Assert.assertEquals(Hex.encodeHexString(sha1), encoded.getPassword());

        encoded = sha1base64.encode(credentials, secret);

        logger.debug("sha1 base64 encoded: {}", encoded);

        Assert.assertEquals(Base64.encodeBase64String(sha1), encoded.getPassword());
    }
}
