package cn.com.xinli.portal.support.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Ehcache wrapper.
 *
 * <p>This class is a simple wrapper for ehcache. It defines a single central
 * ehcache configuration, and all caches required by calling on {@link #createCache(String)}
 * or {@link #createCache(String, int, Collection, boolean, int)} will create
 * an internal cache upon the central configuration.
 *
 * <p>By default, all created caches do not persist in disk (memory only).
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/16.
 */
public class EhcacheManagerAdapter {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhcacheManagerAdapter.class);

    /** Ehcache configuration. */
    private final Configuration ehcacheConfig;

    /** Default max entries local heap. */
    private static final int DEFAULT_MAX_ENTRIES_LOCAL_HEAP = 1000;

    /** Default persistence strategy: not repository in disk (memory only). */
    private static final PersistenceConfiguration.Strategy DEFAULT_STRATEGY =
            PersistenceConfiguration.Strategy.NONE;

    public EhcacheManagerAdapter(String name) {
        ehcacheConfig = new Configuration();
        ehcacheConfig.setName(name);
    }

    /**
     * Create ehcache manager.
     * @return ehcache manager.
     */
    public CacheManager createManager() {
        return CacheManager.create(ehcacheConfig);
    }

    /**
     * Create a cache search attribute.
     * @param name search name.
     * @param type search type.
     * @param expression object expression.
     * @return cache search attribute.
     */
    public static CacheSearchAttribute search(String name, Class<?> type, String expression) {
        return new CacheSearchAttribute(name, type, expression);
    }

    /**
     * Ehcache search attribute.
     */
    public static class CacheSearchAttribute {
        final String name;
        final Class<?> type;
        final String expression;

        CacheSearchAttribute(String name, Class<?> type, String expression) {
            this.name = name;
            this.type = type;
            this.expression = expression;
        }
    }

    /**
     * Create ehcache searchable.
     * @param attributes search attributes.
     * @return ehcache searchable.
     */
    private Searchable createSearchable(Collection<CacheSearchAttribute> attributes) {
        final Searchable searchable = new Searchable();
        attributes.forEach(attr -> searchable.searchAttribute(
                new SearchAttribute().name(attr.name).type(attr.type).expression(attr.expression)
        ));
        return searchable;
    }

    /**
     * Create Ehcache.
     *
     * <p>The created cache is not searchable.
     * The created cache has max {@value #DEFAULT_MAX_ENTRIES_LOCAL_HEAP} entries local heap.
     * Items in created cache does not expire.
     *
     * @param name cache name.
     */
    public void createCache(String name) {
        createCache(name, DEFAULT_MAX_ENTRIES_LOCAL_HEAP);
    }

    /**
     * Create Ehcache.
     *
     * <p>The created cache is not searchable.
     * Items in created cache does not expire.
     *
     * @param name cache name.
     * @param maxEntries max entries local heap.
     */
    public void createCache(String name, int maxEntries) {
        createCache(name, maxEntries, false, 0);
    }

    /**
     * Create Ehcache.
     *
     * <p>The created cache is not searchable.
     *
     * @param name cache name.
     * @param maxEntries max entries local heap.
     * @param ttlEnabled if ttl enabled.
     * @param ttl ttl in seconds.
     */
    public void createCache(String name, int maxEntries, boolean ttlEnabled, int ttl) {
        createCache(name, maxEntries, Collections.emptyList(), ttlEnabled, ttl);
    }

    /**
     * Create Ehcache.
     *
     * <p>The created cache has give name, max entries local heap.
     * and it's searchable if given search attributes is not empty.
     * Items in created cache expires in ttl seconds if tllEnabled
     * is set to true.
     *
     * @param name cache name.
     * @param maxEntries max entries local heap.
     * @param searchAttributes search attributes.
     * @param ttlEnabled if ttl enabled.
     * @param ttl ttl in seconds.
     */
    public void createCache(String name, int maxEntries,
                            Collection<CacheSearchAttribute> searchAttributes,
                            boolean ttlEnabled,
                            int ttl) {
        CacheConfiguration cache = new CacheConfiguration();

        if (!searchAttributes.isEmpty()) {
            cache.name(name)
                    .timeToIdleSeconds(ttlEnabled ? ttl : 0)
                    .maxEntriesLocalHeap(maxEntries)
                    .searchable(createSearchable(searchAttributes))
                    .diskExpiryThreadIntervalSeconds(10)
                    .persistence(new PersistenceConfiguration()
                            .strategy(DEFAULT_STRATEGY));
        } else {
            cache.name(name)
                    .timeToIdleSeconds(ttlEnabled ? ttl : 0)
                    .maxEntriesLocalHeap(maxEntries)
                    .persistence(new PersistenceConfiguration()
                            .strategy(DEFAULT_STRATEGY));
        }
        ehcacheConfig.addCache(cache);

        if (logger.isTraceEnabled()) {
            logger.trace("cache {} configured, max entries: {}, ttl enabled: {}, ttl: {}",
                    name, maxEntries, ttlEnabled, ttl);
        }
    }

}
