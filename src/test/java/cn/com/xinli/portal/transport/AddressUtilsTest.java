package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.util.CodecUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/2/23.
 */
public class AddressUtilsTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AddressUtilsTest.class);

    @Test
    public void testConvertMac() {
        final String mac1 = "20-CF-30-BB-E9-AF",
                mac2 = "20cf-30bb-e9af",
                mac3 = "20:CF:30:BB:E9:AF";

        byte[] b1 = AddressUtils.convertMac(mac1);
        logger.debug("mac: {}", CodecUtils.bytesToHexString(b1));

        byte[] b2 = AddressUtils.convertMac(mac2);
        logger.debug("mac: {}", CodecUtils.bytesToHexString(b2));

        byte[] b3 = AddressUtils.convertMac(mac3);
        logger.debug("mac: {}", CodecUtils.bytesToHexString(b3));

        Assert.assertArrayEquals(b1, b2);
        Assert.assertArrayEquals(b1, b3);
    }
}
