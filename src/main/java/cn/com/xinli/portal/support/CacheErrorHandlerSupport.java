package cn.com.xinli.portal.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Cache error handler.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class CacheErrorHandlerSupport implements CacheErrorHandler {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CacheErrorHandlerSupport.class);

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        logger.warn("Cache {} get error, object: {}.", cache.getName(), o, e);
        throw e;
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        logger.warn("Cache {} put error, object: {}.", cache.getName(), o, e);
        throw e;
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        logger.warn("Cache {} evict error, object: {}.", cache.getName(), o, e);
        throw e;
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        logger.warn("Cache {} clear error, object: {}.", cache.getName(), e);
        throw e;
    }
}
