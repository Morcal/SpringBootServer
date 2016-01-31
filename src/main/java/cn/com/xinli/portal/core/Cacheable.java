package cn.com.xinli.portal.core;

/**
 * Cache-able.
 *
 * <p>Classes implements this interface can be stored in a java-cache, like
 * <a href="www.ehcache.org">EhCache</a>, or a datastore, like
 * <a href="www.redis.org">REDIS</a>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/25.
 */
@FunctionalInterface
public interface Cacheable {
    /**
     * Get cache key.
     * @return cache key.
     */
    Object getKey();
}
