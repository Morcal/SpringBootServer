package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.Session;
import cn.com.xinli.portal.core.SessionNotFoundException;
import cn.com.xinli.portal.core.SessionStore;
import cn.com.xinli.portal.support.cache.SessionCacheEventListener;
import cn.com.xinli.portal.repository.SessionEntity;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class EhCacheSessionDataStore implements SessionStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhCacheSessionDataStore.class);

    @Autowired
    private Ehcache sessionCache;

    @Autowired
    private SessionCacheEventListener sessionCacheEventListener;

    @Value("${pws.session.tti.enable}") private boolean isSessionTtiEnabled;

    @Value("${pws.session.tti.value}") private int sessionTti;

    @PostConstruct
    public void init() {
        boolean registered = sessionCache.getCacheEventNotificationService()
                .registerListener(sessionCacheEventListener, NotificationScope.LOCAL);

        logger.info("register event listener on session cache: {}.", registered);
    }

    @Scheduled(fixedDelay = 300_000L)
    public void evictExpiredSessions() {
        if (isSessionTtiEnabled) {
            if (logger.isTraceEnabled()) {
                logger.trace("Evicting expired sessions.");
            }
            sessionCache.evictExpiredElements();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Get from cache and update statistics.
     * Since "get" will <em>NOT</em> update statistics, it can not
     * represent "keep alive" action.
     *
     * @param id session id.
     * @return session found or null if not found.
     */
    @Override
    public Session get(long id) throws SessionNotFoundException {
        Element element = sessionCache.get(id);
        if (element == null) {
            throw new SessionNotFoundException(id);
        }
        return (Session) element.getObjectValue();
    }

    @Override
    public void put(Session session) {
        Element element;

        if (isSessionTtiEnabled) {
            element = new Element(
                    session.getId(),
                    session,
                    sessionTti,
                    sessionTti);
        } else {
            /* Create cache element without time to idle and time to live. */
            element = new Element(session.getId(), session);
        }
        element.updateUpdateStatistics();
        sessionCache.put(element);
    }

    @Override
    public boolean exists(long id) {
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
            SessionEntity entity = (SessionEntity) element.getObjectValue();
            if (logger.isTraceEnabled()) {
                logger.trace("session {} last modified at: {}, update to {}.",
                        id, entity.getLastModified(), lastModified);
            }
            entity.setLastModified(lastModified);
            sessionCache.put(element);
        }
    }

    @Override
    public boolean delete(long id) {
        logger.trace("ehcache session data store deleting session {}.", id);
        return sessionCache.remove(id);
    }

    @Override
    public long getLastUpdateTime(long id) {
        Element element = sessionCache.get(id);
        return element == null ? -1L : ((Session) element.getObjectValue()).getLastModified();
    }

    @Override
    public List<Session> find(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("Data store query parameters can not be empty.");
        }

        List<Criteria> criterias = new ArrayList<>();
        for (String key : parameters.keySet()) {
            try {
                Attribute<String> attr = sessionCache.getSearchAttribute(key);
                String value = parameters.get(key);
                if (StringUtils.isEmpty(value) && logger.isDebugEnabled()) {
                    logger.trace("+ query parameter {} is empty, ignored.", key);
                } else {
                    criterias.add(attr.eq(parameters.get(key)));
                }
            } catch (CacheException e) {
                logger.trace("+ EhCache search attribute: {} not found, ignored.", key);
            }
        }

        if (criterias.isEmpty()) {
            logger.warn("+ empty query parameters.");
            return Collections.emptyList();
        }

        Query query = sessionCache.createQuery();
        criterias.forEach(query::addCriteria);

        Results results = query.includeValues().execute();

        return results.all().stream()
                .map(result -> (Session) result.getValue())
                .collect(Collectors.toList());
    }

}
