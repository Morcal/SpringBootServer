package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionStore;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * EhCache based session data store.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/29.
 */
@Service
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

    @Override
    public void init() {
        boolean registered = sessionCache.getCacheEventNotificationService()
                .registerListener(sessionCacheEventListener, NotificationScope.LOCAL);

        logger.info("> register event listener on session cache: {}.", registered);
    }

    @Scheduled(fixedDelay = 10L)
    public void evictExpiredSessions() {
        if (isSessionTtiEnabled) {
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
    public Session get(long id) {
        Element element = sessionCache.getQuiet(id);
        return element == null ? null : (Session) element.getObjectValue();
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
     * Update session in the cache.
     *
     * @param id session id.
     * @return true if session found and updated, false not found.
     */
    @Override
    public boolean update(long id) {
        Element element = sessionCache.get(id);
        if (element == null) {
            return false;
        } else {
            element.updateUpdateStatistics();
            sessionCache.put(element);
            return true;
        }
    }

    @Override
    public boolean delete(long id) {
        return sessionCache.remove(id);
    }

    @Override
    public long getLastUpdateTime(long id) {
        Element element = sessionCache.get(id);
        return element == null ? -1L : element.getLastUpdateTime();
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
                    logger.debug("+ query parameter {} is empty, ignored.", key);
                } else {
                    criterias.add(attr.eq(parameters.get(key)));
                }
            } catch (CacheException e) {
                logger.debug("+ EhCache search attribute: {} not found, ignored.", key);
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
