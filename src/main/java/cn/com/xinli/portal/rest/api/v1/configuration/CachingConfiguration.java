package cn.com.xinli.portal.rest.api.v1.configuration;

import cn.com.xinli.portal.rest.api.v1.auth.CacheKeyGenerator;
import cn.com.xinli.portal.rest.api.v1.auth.RestCacheErrorHandler;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.NamedCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Configuration
@EnableCaching
public class CachingConfiguration implements CachingConfigurer {
    /** Access token cache name. */
    public static final String ACCESS_TOKEN_CACHE_NAME = "access-token-cache";

    /** Access token cache name. */
    public static final String SESSION_TOKEN_CACHE_NAME = "session-token-cache";

    /** Access token cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    /** Access token ttl in seconds. */
    public static final int ACCESS_TOKEN_TTL = 60;

    /** Session token ttl in seconds. */
    public static final int SESSION_TOKEN_TTL = 60;

    /** Challenge ttl in seconds. */
    public static final int CHALLENGE_TTL = 30;

    /** EhCache version. */
    public static final int EHCACHE_VERSION = 1;

    @Bean
    public net.sf.ehcache.CacheManager ehcacheManager() {
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        /* Use ehcache as memory only cache. */
        cacheConfiguration.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        net.sf.ehcache.config.Configuration ehcacheConfig = new net.sf.ehcache.config.Configuration();
        ehcacheConfig.addCache(cacheConfiguration);
        return new net.sf.ehcache.CacheManager(ehcacheConfig);
    }

    @Bean
    public net.sf.ehcache.config.Configuration ehcacheConfiguration() {
        return new net.sf.ehcache.config.Configuration();
    }

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehcacheCacheManager() {
        return net.sf.ehcache.CacheManager.newInstance(ehcacheConfiguration());
    }

    @Bean
    public net.sf.ehcache.Cache accessTokenCache() {
        return ehcacheCacheManager().getCache(ACCESS_TOKEN_CACHE_NAME);
    }

    @Bean
    public net.sf.ehcache.Cache sessionTokenCache() {
        return ehcacheCacheManager().getCache(SESSION_TOKEN_CACHE_NAME);
    }

    @Bean net.sf.ehcache.Cache challengeCache() {
        return ehcacheCacheManager().getCache(CHALLENGE_CACHE_NAME);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehcacheCacheManager());
        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return new NamedCacheResolver(
                cacheManager(),
                ACCESS_TOKEN_CACHE_NAME, SESSION_TOKEN_CACHE_NAME, CHALLENGE_CACHE_NAME
        );
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new CacheKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new RestCacheErrorHandler();
    }
}
