package cn.com.xinli.portal.util;

import cn.com.xinli.portal.TestBase;
import org.junit.Test;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/5.
 */
public class TokenUtilTest extends TestBase {

    @Test
    public void testGenerate() {
        for (int i = 0; i < 10; i++) {
            log.debug(TokenUtil.generate());
        }
    }
}
