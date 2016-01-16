package cn.com.xinli.portal.util;

import cn.com.xinli.portal.protocol.Utils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/30.
 */
public class AddressUtilTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AddressUtilTest.class);

    @Test
    public void testIPtoInteger() {
        final String ipStart = "192.168.3.1", ip = "192.168.3.26", ipEnd = "192.168.3.254";

        int start = Utils.convertIpv4Address(ipStart),
                value = Utils.convertIpv4Address(ip),
                end = Utils.convertIpv4Address(ipEnd);

        logger.debug("start: {}, value: {}, end: {}", start, value, end);

    }
}
