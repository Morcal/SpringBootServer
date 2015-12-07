package cn.com.xinli.portal.util;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public interface TimeLimited {
    /**
     * Check if instance expired.
     * @return true if expired.
     */
    boolean expired();
}
