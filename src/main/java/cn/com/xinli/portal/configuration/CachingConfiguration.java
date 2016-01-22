package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.auth.challenge.Challenge;
import cn.com.xinli.portal.support.cache.CacheErrorHandlerSupport;
import cn.com.xinli.portal.support.cache.EhcacheManagerAdapter;
import cn.com.xinli.portal.core.Certificate;
import cn.com.xinli.portal.core.Session;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.interceptor.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * PWS Caching configurations.
 *
 * <p>This class configures one central Ehcache serving for all system
 * objects via an {@link EhcacheManagerAdapter} bean defined by {@link #ehcacheManagerAdapter()}.
 *
 * <p>Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Configuration
public class CachingConfiguration implements CachingConfigurer {
    /** PWS cache name. */
    private static final String PWS_CACHE_NAME = "pws-cache";

    /** {@linkplain Session} cache name. */
    public static final String SESSION_CACHE_NAME = "session-cache";

    /** {@linkplain Certificate} cache name. */
    public static final String CERTIFICATE_CACHE_NAME = "certificate-cache";

    /** {@linkplain Challenge} cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    public static final String RATE_LIMITING_CACHE_NAME = "rate-limiting-cache";

    /** Max cache MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_SESSION_CACHE_ENTRIES = 10_000;

    /** Max challenge MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_CHALLENGE_CACHE_ENTRIES = 10_000;

    /** Max limiting MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_RATE_LIMITING_CACHE_ENTRIES = 200;

    /** {@link Ehcache} element version. */
    public static final long EHCACHE_VERSION = 1L;

    @Value("${pws.session.tti.enable}") private boolean isSessionTtiEnabled;

    @Value("${pws.session.tti.value}") private int sessionTti;

    @Value("${pws.rest.challenge.ttl}") private int challengeTtl;

    @Value("${pws.rest.rate.limiting.ttl}") private int rateLimitingTtl;

    @Bean
    public EhcacheManagerAdapter ehcacheManagerAdapter() {
        EhcacheManagerAdapter adapter = new EhcacheManagerAdapter(PWS_CACHE_NAME);

        List<EhcacheManagerAdapter.CacheSearchAttribute> attributes = new ArrayList<>();
        attributes.add(EhcacheManagerAdapter.search("ip", String.class, "value.getIp()"));
        attributes.add(EhcacheManagerAdapter.search("mac", String.class, "value.getMac()"));
        attributes.add(EhcacheManagerAdapter.search("nas_id", Long.class, "value.getNasId()"));
        attributes.add(EhcacheManagerAdapter.search("username", String.class, "value.getUsername()"));

        /* Create session cache. */
        adapter.createCache(SESSION_CACHE_NAME,
                MAX_SESSION_CACHE_ENTRIES, attributes, isSessionTtiEnabled, sessionTti);

        /* Create certificate cache. */
        adapter.createCache(CERTIFICATE_CACHE_NAME, 100);

        /* Create challenge cache. */
        adapter.createCache(CHALLENGE_CACHE_NAME, MAX_CHALLENGE_CACHE_ENTRIES, true, challengeTtl);

        /* Create rate limiting cache. */
        adapter.createCache(RATE_LIMITING_CACHE_NAME, MAX_RATE_LIMITING_CACHE_ENTRIES, true, rateLimitingTtl);

        return adapter;
    }

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehcacheManager() {
        return ehcacheManagerAdapter().createManager();
    }

    @Bean
    public Ehcache certificateCache() {
        return ehcacheManager().getEhcache(CERTIFICATE_CACHE_NAME);
    }

    @Bean
    public Ehcache challengeCache() {
        return ehcacheManager().getEhcache(CHALLENGE_CACHE_NAME);
    }

    @Bean
    public Ehcache sessionCache() {
        return ehcacheManager().getEhcache(SESSION_CACHE_NAME);
    }

    @Bean
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
