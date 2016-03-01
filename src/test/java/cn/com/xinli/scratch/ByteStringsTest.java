package cn.com.xinli.scratch;

import cn.com.xinli.portal.transport.TransportUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public class ByteStringsTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ByteStringsTest.class);

    @Test
    public void testByteStrings() {
        byte[] values = { 68, 76, 12, -79, 106, 77 };
        logger.debug("value: {}", TransportUtils.bytesToHexString(values));
    }
}
