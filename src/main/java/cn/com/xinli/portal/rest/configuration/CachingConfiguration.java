package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.rest.auth.CacheKeyGenerator;
import cn.com.xinli.portal.rest.auth.RestCacheErrorHandler;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.token.AccessToken;
import cn.com.xinli.portal.rest.token.SessionToken;
import net.sf.ehcache.Ehcache;
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
 * PWS Caching configurations.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Configuration
@EnableCaching
public class CachingConfiguration implements CachingConfigurer {
    /** {@linkplain AccessToken} cache name. */
    public static final String ACCESS_TOKEN_CACHE_NAME = "access-token-cache";

    /** {@linkplain SessionToken} cache name. */
    public static final String SESSION_TOKEN_CACHE_NAME = "session-token-cache";

    /** {@linkplain Challenge} cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    /** {@linkplain Certificate} cache name. */
    public static final String CERTIFICATE_CACHE_NAME = "certificate-cache";

    /** Access token ttl in seconds. */
    public static final int ACCESS_TOKEN_TTL = 25;

    /** Session token ttl in seconds. */
    public static final int SESSION_TOKEN_TTL = 20;

    /** Challenge ttl in seconds. */
    public static final int CHALLENGE_TTL = 10;

    /* Max cache entries. */
    private static final long MAX_CACHE_ENTRIES = 10_000;

    /** EhCache version. */
    public static final int EHCACHE_VERSION = 1;

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehcacheManager() {
        /* Use ehcache as memory only cache. */
        CacheConfiguration accessTokenCache = new CacheConfiguration(),
                sessionTokenCache = new CacheConfiguration(),
                challengeCache = new CacheConfiguration(),
                certificateCache = new CacheConfiguration();

        net.sf.ehcache.config.Configuration ehcacheConfig = new net.sf.ehcache.config.Configuration();

        /* Add access token cache. */
        accessTokenCache.setName(ACCESS_TOKEN_CACHE_NAME);
        accessTokenCache.setTimeToLiveSeconds(ACCESS_TOKEN_TTL);
        accessTokenCache.setMaxEntriesLocalHeap(MAX_CACHE_ENTRIES);
        accessTokenCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(accessTokenCache);

        /* Add session token cache. */
        sessionTokenCache.setName(SESSION_TOKEN_CACHE_NAME);
        sessionTokenCache.setTimeToLiveSeconds(SESSION_TOKEN_TTL);
        sessionTokenCache.setMaxEntriesLocalHeap(MAX_CACHE_ENTRIES);
        sessionTokenCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(sessionTokenCache);

        /* Add challenge token cache. */
        challengeCache.setName(CHALLENGE_CACHE_NAME);
        challengeCache.setTimeToLiveSeconds(CHALLENGE_TTL);
        challengeCache.setMaxEntriesLocalHeap(MAX_CACHE_ENTRIES);
        challengeCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(challengeCache);

        /* Add certificate cache. */
        certificateCache.setName(CERTIFICATE_CACHE_NAME);
        certificateCache.setTimeToLiveSeconds(Long.MAX_VALUE);
        certificateCache.setMaxEntriesLocalHeap(100);
        certificateCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(certificateCache);

        ehcacheConfig.setName("rest-service-cache");

        return  net.sf.ehcache.CacheManager.create(ehcacheConfig);
    }

    @Bean
    public Ehcache accessTokenCache() {
        return ehcacheManager().getEhcache(ACCESS_TOKEN_CACHE_NAME);
    }

    @Bean
    public Ehcache sessionTokenCache() {
        return ehcacheManager().getEhcache(SESSION_TOKEN_CACHE_NAME);
    }

    @Bean
    public Ehcache challengeCache() {
        return ehcacheManager().getEhcache(CHALLENGE_CACHE_NAME);
    }

    @Bean
    public Ehcache certificateCache() {
        return ehcacheManager().getEhcache(CERTIFICATE_CACHE_NAME);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        EhCacheCacheManager cacheManager = new EhCacheCacheManager();
        cacheManager.setCacheManager(ehcacheManager());
        return cacheManager;
    }

    @Override
    public CacheResolver cacheResolver() {
        return new NamedCacheResolver(
                cacheManager(),
                ACCESS_TOKEN_CACHE_NAME, SESSION_TOKEN_CACHE_NAME, CHALLENGE_CACHE_NAME, CERTIFICATE_CACHE_NAME
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
