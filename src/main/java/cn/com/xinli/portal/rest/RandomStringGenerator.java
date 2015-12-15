package cn.com.xinli.portal.rest;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
public interface RandomStringGenerator {

    /**
     * Generate an unique secure random string.
     * @return unique secure random string.
     */
    String generateUniqueRandomString();
}
