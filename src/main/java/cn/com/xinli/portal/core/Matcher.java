package cn.com.xinli.portal.core;

/**
 * Generic matcher.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
@FunctionalInterface
public interface Matcher<T> {
    /**
     * Check if this rule matches given credentials.
     * @param value value to match.
     * @return true if matches.
     */
    boolean matches(T value);
}
