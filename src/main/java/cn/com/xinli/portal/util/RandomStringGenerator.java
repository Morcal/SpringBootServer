package cn.com.xinli.portal.util;

/**
 * Random string generator.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
public interface RandomStringGenerator {
    /**
     * Generate an unique secure random string.
     *
     * @param size string size.
     * @return unique secure random string.
     */
    String generateUniqueRandomString(int size);
}
