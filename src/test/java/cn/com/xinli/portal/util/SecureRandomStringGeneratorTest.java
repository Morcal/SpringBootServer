package cn.com.xinli.portal.util;

import cn.com.xinli.portal.TestBase;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/26.
 */
public class SecureRandomStringGeneratorTest extends TestBase {
    final int TEST_STRINGS_COUNT = 10000;
    final int TEST_STRINGS_SIZE = 256;

    @Test
    public void testGenerateSecureRandomStrings() {
        Set<String> strings = new HashSet<>();
        SecureRandomStringGenerator random = new SecureRandomStringGenerator();
        long took = System.nanoTime();

        for (int i = 0; i < TEST_STRINGS_COUNT; i++) {
            strings.add(random.generateUniqueRandomString(TEST_STRINGS_SIZE));
        }

        took = System.nanoTime() - took;
        logger.debug("Generate {} unique strings with size:  {} took {} milliseconds.",
                TEST_STRINGS_COUNT, TEST_STRINGS_SIZE, took / 1_000_000L);

        Assert.assertEquals(TEST_STRINGS_COUNT, strings.size());
    }
}
