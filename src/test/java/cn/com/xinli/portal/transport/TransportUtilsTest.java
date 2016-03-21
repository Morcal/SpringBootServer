package cn.com.xinli.portal.transport;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public class TransportUtilsTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(TransportUtilsTest.class);

    @Test
    public void testConvertMac() {
        final String mac1 = "20-CF-30-BB-E9-AF",
                mac2 = "20cf-30bb-e9af",
                mac3 = "20:CF:30:BB:E9:AF";

        byte[] b1 = TransportUtils.convertMac(mac1);
        logger.debug("mac: {}", TransportUtils.bytesToHexString(b1));

        byte[] b2 = TransportUtils.convertMac(mac2);
        logger.debug("mac: {}", TransportUtils.bytesToHexString(b2));

        byte[] b3 = TransportUtils.convertMac(mac3);
        logger.debug("mac: {}", TransportUtils.bytesToHexString(b3));

        Assert.assertArrayEquals(b1, b2);
        Assert.assertArrayEquals(b1, b3);
    }
}
