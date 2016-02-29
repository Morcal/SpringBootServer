package cn.com.xinli.scratch;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/2/25.
 */
public class CodecTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CodecTest.class);

    @Test
    public void testTokenCodec() {
        final String value = "Hello, world! 你好, 世界!";

        final String encoded = Utf8.decode(Base64.encode(Utf8.encode(value)));
        logger.debug("encoded: {}", encoded);

        final String decoded = Utf8.decode(Base64.decode(Utf8.encode(encoded)));
        logger.debug("decoded: {}", decoded);

        Assert.assertEquals(value, decoded);
    }
}
