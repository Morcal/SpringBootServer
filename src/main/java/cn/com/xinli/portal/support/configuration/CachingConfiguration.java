package cn.com.xinli.portal.support.configuration;

import cn.com.xinli.portal.core.Context;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.support.ehcache.EhcacheManagerAdapter;
import cn.com.xinli.portal.support.ehcache.SessionSearchable;
import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.util.SerializerAdapter;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import cn.com.xinli.portal.web.auth.token.DelimitedTokeKeySerializer;
import cn.com.xinli.portal.web.auth.token.TokenKey;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * PWS Caching configurations.
 *
 * <p>This class configures one central Ehcache serving for all system
 * objects via an {@link EhcacheManagerAdapter} bean defined by {@link #ehcacheManagerAdapter()}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
@Configuration
@Profile("standalone")
public class CachingConfiguration {
    /** PWS cache name. */
    private static final String PWS_CACHE_NAME = "pws-cache";

    /** {@linkplain Session} cache name. */
    public static final String SESSION_CACHE_NAME = "session-cache";

    /** {@linkplain Certificate} cache name. */
    public static final String CERTIFICATE_CACHE_NAME = "certificate-cache";

    /** {@linkplain Challenge} cache name. */
    public static final String CHALLENGE_CACHE_NAME = "challenge-cache";

    /** Rate limiting cache name. */
    public static final String RATE_LIMITING_CACHE_NAME = "rate-limiting-cache";

    /** NAS cache name. */
    public static final String NAS_CACHE_NAME = "nas-cache";

    /** NAS search cache name. */
    public static final String NAS_SEARCH_CACHE_NAME = "nas-search-cache";

    /** NAS rule cache name. */
    public static final String NAS_RULE_CACHE_NAME = "nas-rule-cache";

    /** NAS mapping cache name. */
    public static final String NAS_MAPPING_CACHE_NAME = "nas-mapping-cache";

    /** Radius server cache name. */
    public static final String RADIUS_CACHE_NAME = "radius-cache";

    /** Session search cache name. */
    public static final String SESSION_SEARCH_CACHE_NAME = "session-search-cache";

    /** Max supported NAS/BRAS devices, rules, certificates. */
    private static final int DEFAULT_MAX_SUPPORTED = 1000;

    /** Max session cache entries. */
    private static final int MAX_SESSION_CACHE_ENTRIES = 50_000;

    /** Max challenge  cache entries. */
    private static final int MAX_CHALLENGE_CACHE_ENTRIES = 10_000;

    /** Max limiting  cache entries. */
    private static final int MAX_RATE_LIMITING_CACHE_ENTRIES = 200;

    /** Max session search cache entries. */
    private static final int MAX_SESSION_SEARCH_CACHE_ENTRIES = 50_000;

    /** Max NAS/BRAS-user mapping cache entries. */
    private static final int MAX_NAS_MAPPING_ENTRIES = 100_000;

    /** {@link Ehcache} element version. */
    public static final long EHCACHE_VERSION = 1L;

    /** Token key delimiter. */
    private static final String TOKEN_KEY_DELIMITER = "::";

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Bean
    public Serializer<Session> sessionSerializer() {
        return new SerializerAdapter<>(Session.class);
    }

    @Bean
    public Serializer<Context> contextSerializer() {
        return new SerializerAdapter<>(Context.class);
    }

    @Bean
    public Serializer<Nas> nasSerializer() {
        return new SerializerAdapter<>(Nas.class);
    }

    @Bean
    public Serializer<NasRule> nasRuleSerializer() {
        return new SerializerAdapter<>(NasRule.class);
    }

    @Bean(name = "delimiterTokenKeySerializer")
    public Serializer<TokenKey> delimiterTokenKeySerializer() {
        return new DelimitedTokeKeySerializer(TOKEN_KEY_DELIMITER);
    }

    @Bean(name = "jsonTokenKeySerializer")
    public Serializer<TokenKey> jsonTokenKeySerializer() {
        return new SerializerAdapter<>(TokenKey.class);
    }

    @Bean
    public EhcacheManagerAdapter ehcacheManagerAdapter() {
        EhcacheManagerAdapter adapter = new EhcacheManagerAdapter(PWS_CACHE_NAME);

        /* Create session cache. */
        adapter.createCache(SESSION_CACHE_NAME, MAX_SESSION_CACHE_ENTRIES);

        /* Create session search cache. */
        adapter.createCache(SESSION_SEARCH_CACHE_NAME,
                MAX_SESSION_SEARCH_CACHE_ENTRIES, new SessionSearchable(),
                serverConfiguration.getSessionConfiguration().isEnableTtl(),
                serverConfiguration.getSessionConfiguration().getTtl());

        /* Create certificate cache. */
        adapter.createCache(CERTIFICATE_CACHE_NAME, DEFAULT_MAX_SUPPORTED);

        /* Create nas cache. */
        adapter.createCache(NAS_CACHE_NAME, DEFAULT_MAX_SUPPORTED);

        /* Create nas rule cache. */
        adapter.createCache(NAS_RULE_CACHE_NAME, DEFAULT_MAX_SUPPORTED);

        /* Create nas search cache. */
        adapter.createCache(NAS_SEARCH_CACHE_NAME, DEFAULT_MAX_SUPPORTED);

        /* Create nas mapping cache. */
        adapter.createCache(NAS_MAPPING_CACHE_NAME, MAX_NAS_MAPPING_ENTRIES);

        /* Create radius cache. */
        adapter.createCache(RADIUS_CACHE_NAME, DEFAULT_MAX_SUPPORTED);

        /* Create challenge cache. */
        adapter.createCache(CHALLENGE_CACHE_NAME, MAX_CHALLENGE_CACHE_ENTRIES, true,
                serverConfiguration.getRestConfiguration().getChallengeTtl());

        /* Create rate limiting cache. */
        adapter.createCache(RATE_LIMITING_CACHE_NAME, MAX_RATE_LIMITING_CACHE_ENTRIES, true,
                serverConfiguration.getRateLimitingConfiguration().getTtl());

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

    @Bean
    public Ehcache nasCache() {
        return ehcacheManager().getEhcache(NAS_CACHE_NAME);
    }

    @Bean
    public Ehcache nasRuleCache() {
        return ehcacheManager().getEhcache(NAS_RULE_CACHE_NAME);
    }

    @Bean
    public Ehcache nasSearchCache() {
        return ehcacheManager().getEhcache(NAS_SEARCH_CACHE_NAME);
    }

    @Bean
    public Ehcache nasMappingCache() {
        return ehcacheManager().getEhcache(NAS_MAPPING_CACHE_NAME);
    }

    @Bean
    public Ehcache radiusCache() {
        return ehcacheManager().getEhcache(RADIUS_CACHE_NAME);
    }

    @Bean
    public Ehcache sessionSearchCache() {
        return ehcacheManager().getEhcache(SESSION_SEARCH_CACHE_NAME);
    }
}
