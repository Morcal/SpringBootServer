package cn.com.xinli.scratch;

import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class MD5Test {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(MD5Test.class);

    @Test
    public void testMD5() {
        final String value = "The quick brown fox jumps over a lazy dog.";

        String sum = Md5Crypt.md5Crypt(value.getBytes());
        logger.debug(sum);
    }
}
