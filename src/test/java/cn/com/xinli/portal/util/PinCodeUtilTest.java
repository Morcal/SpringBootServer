package cn.com.xinli.portal.util;

import cn.com.xinli.portal.core.ServerException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PIN code test.
 * @author zhoupeng, created on 2016/4/13.
 */
public class PinCodeUtilTest {
    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(PinCodeUtil.class);

    @Test
    public void testGeneratePIN() throws ServerException {
        final String data = "abc", key = "def";
        final String PIN = PinCodeUtil.generatePIN(data, key, System.currentTimeMillis());
        logger.debug("pin: {}", PIN);
    }

    @Test
    public void testGeneratePIN2() throws ServerException {
        final String data = "15029232580", key = "ajf;#!@#81&^!*";
        final String PIN = PinCodeUtil.generatePIN(data, key, System.currentTimeMillis());
        logger.debug("pin: {}", PIN);
    }

    @Test
    public void testEscape() {
        final String prefix = "\\r\\2";

        logger.debug("prefix: {}", prefix);

        String username = prefix + "foobar";

        logger.debug("username: {}", username);
    }
}
