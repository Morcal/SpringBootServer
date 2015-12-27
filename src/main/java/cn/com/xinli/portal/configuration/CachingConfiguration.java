package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.support.CacheErrorHandlerSupport;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
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
    /** {@linkplain Session} cache name. */
    public static final String SESSION_CACHE_NAME = "session-cache";

    /** {@linkplain Certificate} cache name. */
    public static final String CERTIFICATE_CACHE_NAME = "certificate-cache";

    /** {@linkplain Challenge} cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    /** Max cache entries. */
    private static final long MAX_SESSION_CACHE_ENTRIES = 10_000;

    /** Max challenge entries. */
    private static final long MAX_CHALLENGE_CACHE_ENTRIES = 10_000;

    /** EhCache version. */
    public static final long EHCACHE_VERSION = 1L;

    @Autowired
    private ServerConfig serverConfig;

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehcacheManager() {
        /* Use ehcache as memory only cache. */
        CacheConfiguration sessionCache = new CacheConfiguration(),
                challengeCache = new CacheConfiguration(),
                certificateCache = new CacheConfiguration();

        net.sf.ehcache.config.Configuration ehcacheConfig = new net.sf.ehcache.config.Configuration();

        /* Add session token cache. */
        sessionCache.setName(SESSION_CACHE_NAME);
        sessionCache.setTimeToLiveSeconds(serverConfig.isEnableSessionTtl() ? serverConfig.getSessionTtl() : 0);
        sessionCache.setMaxEntriesLocalHeap(MAX_SESSION_CACHE_ENTRIES);
        sessionCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(sessionCache);

        /* Add certificate cache. */
        certificateCache.setName(CERTIFICATE_CACHE_NAME);
        certificateCache.setTimeToLiveSeconds(0);
        certificateCache.setMaxEntriesLocalHeap(100);
        certificateCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(certificateCache);

        /* Add challenge token cache. */
        challengeCache.setName(CHALLENGE_CACHE_NAME);
        challengeCache.setTimeToLiveSeconds(serverConfig.getChallengeTtl());
        challengeCache.setMaxEntriesLocalHeap(MAX_CHALLENGE_CACHE_ENTRIES);
        challengeCache.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(challengeCache);

        ehcacheConfig.setName("service-cache");

        return  net.sf.ehcache.CacheManager.create(ehcacheConfig);
    }

    @Bean
    public Ehcache certificateCache() {
        return ehcacheManager().getEhcache(CERTIFICATE_CACHE_NAME);
    }

    @Bean
    public Ehcache challengeCache() {
        return ehcacheManager().getEhcache(CHALLENGE_CACHE_NAME);
    }

    public Ehcache sessionCache() {
        return ehcacheManager().getEhcache(SESSION_CACHE_NAME);
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
                SESSION_CACHE_NAME, CERTIFICATE_CACHE_NAME, CHALLENGE_CACHE_NAME
        );
    }

    @Override
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandlerSupport();
    }
}
