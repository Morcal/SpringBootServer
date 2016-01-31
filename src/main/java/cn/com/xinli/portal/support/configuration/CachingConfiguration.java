package cn.com.xinli.portal.support.configuration;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.support.ehcache.NasSerializerAdapter;
import cn.com.xinli.portal.support.ehcache.SessionSerializerAdapter;
import cn.com.xinli.portal.support.ehcache.EhcacheManagerAdapter;
import cn.com.xinli.portal.util.Serializer;
import cn.com.xinli.portal.web.auth.challenge.Challenge;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

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

    /** NAS mapping cache name. */
    public static final String NAS_MAPPING_CACHE_NAME = "nas-mapping-cache";

    /** Max cache MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_SESSION_CACHE_ENTRIES = 10_000;

    /** Max challenge MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_CHALLENGE_CACHE_ENTRIES = 10_000;

    /** Max limiting MESSAGE_TRANSLATE_TABLE. */
    private static final int MAX_RATE_LIMITING_CACHE_ENTRIES = 200;

    /** {@link Ehcache} element version. */
    public static final long EHCACHE_VERSION = 1L;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Bean
    public Serializer<Session> sessionSerializer() {
        return new SessionSerializerAdapter();
    }

    @Bean
    public Serializer<Nas> nasSerializer() {
        return new NasSerializerAdapter();
    }

    @Bean
    public EhcacheManagerAdapter ehcacheManagerAdapter() {
        EhcacheManagerAdapter adapter = new EhcacheManagerAdapter(PWS_CACHE_NAME);

        List<EhcacheManagerAdapter.CacheSearchAttribute> attributes = new ArrayList<>();
        attributes.add(EhcacheManagerAdapter.search("ip", String.class, "value.getIp()"));
        attributes.add(EhcacheManagerAdapter.search("mac", String.class, "value.getMac()"));
        attributes.add(EhcacheManagerAdapter.search("nas", String.class, "value.getNas()"));
        attributes.add(EhcacheManagerAdapter.search("username", String.class, "value.getUsername()"));

        /* Create session cache. */
        adapter.createCache(SESSION_CACHE_NAME,
                MAX_SESSION_CACHE_ENTRIES, attributes,
                serverConfiguration.getSessionConfiguration().isEnableTtl(),
                serverConfiguration.getSessionConfiguration().getTtl());

        /* Create certificate cache. */
        adapter.createCache(CERTIFICATE_CACHE_NAME, 1000);

        /* Create nas cache. */
        adapter.createCache(NAS_CACHE_NAME, 1000);

        /* Create nas cache. */
        adapter.createCache(NAS_MAPPING_CACHE_NAME, 100_000);

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
    public Ehcache nasMappingCache() {
        return ehcacheManager().getEhcache(NAS_MAPPING_CACHE_NAME);
    }
}
