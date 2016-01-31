package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.SessionConfiguration;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.support.repository.SessionRepository;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EhCache based session data store.
 *
 * <p>This class implements a session data store based on
 * <a href="http://ehcache.org">Ehcache</a>.
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

    @Qualifier("sessionRepository")
    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private Ehcache sessionCache;

    @Autowired
    private Serializer<Session> sessionSerializer;

    @Autowired
    private SessionCacheEventListener sessionCacheEventListener;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @PostConstruct
    public void init() {
        boolean registered = sessionCache.getCacheEventNotificationService()
                .registerListener(sessionCacheEventListener, NotificationScope.LOCAL);

        logger.info("register event listener on session cache: {}.", registered);

        for (Session session : sessionRepository.findAll()) {
            put(session);
        }

        logger.info("EhCache sync with database done.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
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

    Session fromValue(Object value) {
        return sessionSerializer.deserialize((byte[]) value);
    }

    Session fromElement(Element element) {
        Objects.requireNonNull(element);
        return fromValue(element.getObjectValue());
    }

    Element toElement(Session session) {
        Objects.requireNonNull(session);
        try {
            final String key = String.valueOf(session.getId());
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
        } catch (SerializationException e) {
            logger.error("Failed to serialize session", e);
            return null;
        }
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
        sessionRepository.save(session);

        Element element = toElement(session);
        if (element != null) {
            element.updateUpdateStatistics();
            sessionCache.put(element);
        } else {
            logger.error("Failed to save session in cache, {}", session);
        }
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
    public boolean delete(Long id) {
        logger.trace("ehcache session data store deleting session {}.", id);
        sessionRepository.delete(id);
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
    List<Session> findInCache(Map<String, String> parameters) {
        List<Criteria> criteria = new ArrayList<>();
        for (String key : parameters.keySet()) {
            try {
                Attribute<String> attr = sessionCache.getSearchAttribute(key);
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
            return Collections.emptyList();
        }

        Query query = sessionCache.createQuery();
        criteria.forEach(query::addCriteria);

        Results results = query.includeValues().execute();

        return results.all().stream()
                .map(result -> fromValue(result.getValue()))
                .collect(Collectors.toList());
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
    public List<Session> find(String ip) {
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
    public List<Session> find(String ip, String mac) {
        Objects.requireNonNull(ip);
        Objects.requireNonNull(mac);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("ip", ip);
        parameters.put("mac", mac);

        return findInCache(parameters);
    }
}
