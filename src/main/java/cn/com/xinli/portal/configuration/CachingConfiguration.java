package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.auth.Certificate;
import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.support.CacheErrorHandlerSupport;
import cn.com.xinli.portal.support.SessionCacheEventListener;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
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
public class CachingConfiguration implements CachingConfigurer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CachingConfiguration.class);

    /** {@linkplain Session} cache name. */
    public static final String SESSION_CACHE_NAME = "session-cache";

    /** {@linkplain Certificate} cache name. */
    public static final String CERTIFICATE_CACHE_NAME = "certificate-cache";

    /** {@linkplain Challenge} cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    public static final String RATE_LIMITING_CACHE_NAME = "rate-limiting-cache";

    /** Max cache entries. */
    private static final int MAX_SESSION_CACHE_ENTRIES = 10_000;

    /** Max challenge entries. */
    private static final int MAX_CHALLENGE_CACHE_ENTRIES = 10_000;

    /** Max limiting entries. */
    private static final int MAX_RATE_LIMITING_CACHE_ENTRIES = 200;

    /** {@link Ehcache} element version. */
    public static final long EHCACHE_VERSION = 1L;

    @Value("${pws.session.tti.enable}") private boolean isSessionTtiEnabled;

    @Value("${pws.session.tti.value}") private int sessionTti;

    @Value("${pws.rest.challenge.ttl}") private int challengeTtl;

    @Value("${pws.rest.rate.limiting.ttl}") private int rateLimitingTtl;

    @Bean(name = "session-cache-event-listener")
    public CacheEventListener sessionCacheEventListener() {
        return new SessionCacheEventListener();
    }

    @Bean(name = "ehcache-manager", destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehcacheManager() {
        /* Use ehcache as memory only cache. */
        CacheConfiguration sessionCache = new CacheConfiguration(),
                challengeCache = new CacheConfiguration(),
                certificateCache = new CacheConfiguration(),
                rateLimitingCache = new CacheConfiguration();

        net.sf.ehcache.config.Configuration ehcacheConfig = new net.sf.ehcache.config.Configuration();

        /* Add session token cache. */
        Searchable searchable = new Searchable();
        searchable.searchAttribute(new SearchAttribute().name("ip").type(String.class).expression("value.getIp()"))
                .searchAttribute(new SearchAttribute().name("mac").type(String.class).expression("value.getMac()"))
                .searchAttribute(new SearchAttribute().name("nas_id").type(Long.class).expression("value.getNasId()"))
                .searchAttribute(new SearchAttribute().name("username").type(String.class).expression("value.getUsername()"));

        logger.info("> session ttl enabled: {}, ttl: {}", isSessionTtiEnabled, sessionTti);

        sessionCache.name(SESSION_CACHE_NAME)
                .timeToIdleSeconds(isSessionTtiEnabled ? sessionTti : 0)
                .maxEntriesLocalHeap(MAX_SESSION_CACHE_ENTRIES)
                .searchable(searchable)
                .diskExpiryThreadIntervalSeconds(10)
                .persistence(new PersistenceConfiguration()
                        .strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(sessionCache);

        /* Add certificate cache. */
        certificateCache.name(CERTIFICATE_CACHE_NAME)
                .eternal(true)
                .maxEntriesLocalHeap(100)
                .persistence(new PersistenceConfiguration()
                        .strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(certificateCache);

        /* Add challenge token cache. */
        challengeCache.name(CHALLENGE_CACHE_NAME)
                .timeToLiveSeconds(challengeTtl)
                .maxEntriesLocalHeap(MAX_CHALLENGE_CACHE_ENTRIES)
                .persistence(new PersistenceConfiguration()
                        .strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(challengeCache);

        /* Add rate-limiting cache. */
        rateLimitingCache.name(RATE_LIMITING_CACHE_NAME)
                .timeToIdleSeconds(rateLimitingTtl)
                .maxEntriesLocalHeap(MAX_RATE_LIMITING_CACHE_ENTRIES)
                .persistence(new PersistenceConfiguration()
                        .strategy(PersistenceConfiguration.Strategy.NONE));
        ehcacheConfig.addCache(rateLimitingCache);

        ehcacheConfig.setName("service-cache");

        return net.sf.ehcache.CacheManager.create(ehcacheConfig);
    }

    @Bean(name = "certificateCache")
    public Ehcache certificateCache() {
        return ehcacheManager().getEhcache(CERTIFICATE_CACHE_NAME);
    }

    @Bean(name = "challengeCache")
    public Ehcache challengeCache() {
        return ehcacheManager().getEhcache(CHALLENGE_CACHE_NAME);
    }

    @Bean(name = "sessionCache")
    public Ehcache sessionCache() {
        return ehcacheManager().getEhcache(SESSION_CACHE_NAME);
    }

    @Bean(name = "rateLimitingCache")
    public Ehcache rateLimitingCache() {
        return ehcacheManager().getEhcache(RATE_LIMITING_CACHE_NAME);
    }

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
