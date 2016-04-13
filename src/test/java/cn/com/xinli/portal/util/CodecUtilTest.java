package cn.com.xinli.portal.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhoupeng, created on 2016/4/13.
 */
public class CodecUtilTest {
    private final Logger logger = LoggerFactory.getLogger(CodecUtilTest.class);

    @Test
    public void testUnescapeString() {
        final String text = "\\r\\2abcdef";

        logger.debug(text);

        logger.debug("unescaped: {}", CodecUtils.unescapeString(text));
    }
}
