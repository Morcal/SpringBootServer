package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.SessionConfiguration;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.support.persist.SessionPersistence;
import cn.com.xinli.portal.util.Serializer;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Results;
import net.sf.ehcache.search.expression.Criteria;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EhCache based session data store.
 *
 * <p>This class implements a session data store based on
 * <a href="http://ehcache.org">Ehcache</a>.
 * Each {@link Session} is saved in two {@link Ehcache}s,
 * {@link #sessionCache} is <code>{session id: full-populated session}</code>,
 * the other {@link #sessionSearchCache} is <code>{session id: session-search-content}</code>.
 *
 * <p>Session eviction implemented by schedule a fixed delay
 * spring-task which evicts expired {@link Session}s periodically.
 *
 * <p>This class registers a Ehcache event listener so that
 * it can perform actions when elements in the cache expires.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/29.
 */
@Component
@EnableScheduling
@Profile("standalone")
public class EhcacheSessionDataStore implements SessionStore, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhcacheSessionDataStore.class);

    @Autowired
    private Ehcache sessionCache;

    @Autowired
    private Ehcache sessionSearchCache;

    @Autowired
    private SessionPersistence sessionPersistence;

    @Autowired
    private Serializer<Session> sessionSerializer;

    @Autowired
    private SessionCacheEventListener sessionCacheEventListener;

    @Autowired
    private ServerConfiguration serverConfiguration;

    public void init() {
        boolean registered = sessionCache.getCacheEventNotificationService()
                .registerListener(sessionCacheEventListener, NotificationScope.LOCAL);

        logger.info("register event listener on session cache: {}.", registered);

        sessionPersistence.all(this::addSession);

        logger.info("EhCache sync with database done.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionPersistence);
    }

    @Scheduled(fixedDelay = 300_000L)
    public void evictExpiredSessions() {
        if (serverConfiguration.getSessionConfiguration().isEnableTtl()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Evicting expired sessions.");
            }
            sessionCache.evictExpiredElements();
        }
    }

    /**
     * Add a session to caches.
     *
     * <p>{@link #sessionCache} is <code>{session id: full-populated session}</code>,
     * the other {@link #sessionSearchCache} is <code>{session id: session-search-content}</code>.
     * @param session session to add.
     */
    private void addSession(Session session) {
        if (logger.isTraceEnabled()) {
            logger.trace("put session into cache and search cache.");
        }
        sessionCache.put(toElement(session));
        sessionSearchCache.put(toElement(toSearchable(session)));
    }

    /**
     * Create searchable from session.
     * @param session session to create.
     * @return searchable.
     */
    private SessionSearchable toSearchable(Session session) {
        Credentials credentials = session.getCredentials();
        return new SessionSearchable()
                .ip(credentials.getIp())
                .mac(credentials.getMac())
                .username(credentials.getUsername())
                .nas(session.getNas().getName())
                .value(session.getId());
    }

    /**
     * Deserialize cache value to session.
     * @param element cache element.
     * @return session.
     */
    private Session fromElement(Element element) {
        Objects.requireNonNull(element);
        return sessionSerializer.deserialize((byte[]) element.getObjectValue());
    }

    /**
     * Serialize searchable to cache element.
     * @param searchable searchable.
     * @return cache element.
     */
    private Element toElement(SessionSearchable searchable) {
        Objects.requireNonNull(searchable);
        return new Element(searchable.getValue(), searchable);
    }

    /**
     * Serialize session to cache element.
     * @param session session.
     * @return cache element.
     */
    private Element toElement(Session session) {
        Objects.requireNonNull(session);
        final long key = session.getId();
        final byte[] value = sessionSerializer.serialize(session);
        Element element;

        SessionConfiguration config = serverConfiguration.getSessionConfiguration();
        if (config.isEnableTtl()) {
            element = new Element(
                    key,
                    value,
                    config.getTtl(),
                    config.getTtl());
        } else {
            /* Create cache element without time to idle and time to live. */
            element = new Element(key, value);
        }
        return element;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to retrieve session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store, or else
     * throw {@link SessionNotFoundException}. ("Cache-aside" pattern).
     *
     * <p>Get from cache and update statistics.
     * Since "get" will <em>NOT</em> update statistics, it can not
     * represent "keep alive" action.
     *
     * @param id session id.
     * @return session found or null if not found.
     */
    @Override
    public Session get(Long id) throws SessionNotFoundException {
        Element element = sessionCache.get(id);
        if (element == null) {
            throw new SessionNotFoundException(id);
        }
        return fromElement(element);
    }

    @Override
    public void put(Session session) {
        Objects.requireNonNull(session);

        /* Save to database, id will be generated. */
        sessionPersistence.save(session);

        addSession(session);
    }

    @Override
    public boolean exists(Long id) {
        return sessionCache.get(id) != null;
    }

    /**
     * {@inheritDoc}
     *
     * Update session in the cache in steps:
     * <br>1. retrieve session from cache.
     * <br>2. update last modified of session.
     * <br>3. put session back to cache.
     *
     * @param id session id.
     * @param lastModified last modified time (UNIX epoch time).
     */
    @Override
    public void update(long id, long lastModified) throws SessionNotFoundException {
        Element element = sessionCache.get(id);
        if (element == null) {
            throw new SessionNotFoundException(id);
        } else {
            Session session = fromElement(element);
            if (logger.isTraceEnabled()) {
                logger.trace("session {} last modified at: {}, update to {}.",
                        id, session.getLastModified(), lastModified);
            }
            session.setLastModified(lastModified);
            put(session);
        }
    }

    @Override
    public boolean delete(Session session) {
        Objects.requireNonNull(session);
        sessionPersistence.delete(session);
        sessionSearchCache.remove(session.getId());
        return sessionCache.remove(session.getId());
    }

    @Override
    public boolean delete(Long id) {
        logger.trace("ehcache session data store deleting session {}.", id);
        sessionPersistence.delete(id);
        sessionSearchCache.remove(id);
        return sessionCache.remove(id);
    }

    @Override
    public long getLastUpdateTime(Long id) throws SessionNotFoundException {
        Element element = sessionCache.get(id);

        if (element == null) {
            throw new SessionNotFoundException(id);
        }

        Session session = fromElement(element);
        return session.getLastModified();
    }

    /**
     * Find cached session in cache.
     * @param parameters find parameters.
     * @return list of cached sessions.
     */
    private Set<Session> findInCache(Map<String, String> parameters) {
        List<Criteria> criteria = new ArrayList<>();
        for (String key : parameters.keySet()) {
            try {
                Attribute<String> attr = sessionSearchCache.getSearchAttribute(key);
                String value = parameters.get(key);
                if (StringUtils.isEmpty(value) && logger.isDebugEnabled()) {
                    logger.trace("+ query parameter {} is empty, ignored.", key);
                } else {
                    criteria.add(attr.eq(parameters.get(key)));
                }
            } catch (CacheException e) {
                logger.trace("+ EhCache search attribute: {} not found, ignored.", key);
            }
        }

        if (criteria.isEmpty()) {
            logger.warn("+ empty query parameters.");
            return Collections.emptySet();
        }

        if (logger.isTraceEnabled()) {
            logger.trace("search session with parameter: {}", parameters);
        }

        Query query = sessionSearchCache.createQuery();
        criteria.forEach(query::addCriteria);

        Results results = query.includeKeys().execute();

        List<Long> keys = results.all().stream()
                .map(result -> (Long) result.getKey())
                .collect(Collectors.toList());

        return sessionCache.getAll(keys).values().stream()
                .map(this::fromElement)
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to find session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store("Cache-aside" pattern).
     *
     * @param ip user ip address.
     * @return result.
     */
    @Override
    public Set<Session> find(String ip) {
        Objects.requireNonNull(ip);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ip", ip);

        return findInCache(parameters);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to find session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store("Cache-aside" pattern).
     *
     * @param ip user ip address.
     * @param mac user mac address.
     * @return result.
     */
    @Override
    public Set<Session> find(String ip, String mac) {
        Objects.requireNonNull(ip);
        Objects.requireNonNull(mac);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ip", ip);
        parameters.put("mac", mac);

        return findInCache(parameters);
    }
}
