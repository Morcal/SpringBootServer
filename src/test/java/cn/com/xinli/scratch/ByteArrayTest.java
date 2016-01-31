package cn.com.xinli.scratch;

import org.apache.commons.codec.binary.Hex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/26.
 */
public class ByteArrayTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ByteArrayTest.class);

    @Test
    public void testByteArray() throws UnknownHostException {
        byte[] array = InetAddress.getByName("192.168.3.26").getAddress();
        logger.debug(Arrays.toString(array));
        String string = Hex.encodeHexString(array);
        logger.debug(string);
    }
}
