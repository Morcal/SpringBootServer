package cn.com.xinli.portal.core;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class PortalErrorTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalErrorTest.class);

    @Test
    public void testPortalError() {
        boolean invalid = false;
        try {
            PortalError.of(1001);
        } catch (IllegalArgumentException e) {
            invalid = true;
        }

        Assert.assertTrue(invalid);

        PortalError error = PortalError.of(101);
        Assert.assertNotNull(error);

        Assert.assertNotNull(error.getReason());

        logger.debug("error 101: {}", error);
    }
}
