package cn.com.xinli.portal.rest.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class RestCacheErrorHandler implements CacheErrorHandler {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
        log.warn("Cache " +  cache.getName() + "get error, " + o.toString(), e);
        throw e;
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
        log.warn("Cache " +  cache.getName() + " put error, " + o.toString(), e);
        throw e;
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
        log.warn("Cache " +  cache.getName() + " evict error, " + o.toString(), e);
        throw e;
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.warn("Cache " +  cache.getName() + " clear error, ", e);
        throw e;
    }
}
