package cn.com.xinli.portal.scratch;

import cn.com.xinli.portal.TestBase;
import org.junit.Test;
import org.springframework.security.crypto.codec.Hex;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/26.
 */
public class ByteArrayTest extends TestBase {

    @Test
    public void testByteArray() throws UnknownHostException {
        byte[] array = InetAddress.getByName("192.168.3.26").getAddress();
        logger.debug(Arrays.toString(array));
        String string = new String(Hex.encode(array));
        logger.debug(string);
    }
}
