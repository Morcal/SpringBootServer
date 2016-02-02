package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionNotFoundException;
import cn.com.xinli.portal.core.session.SessionStore;
import cn.com.xinli.portal.support.configuration.ClusterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * REDIS based session store.
 *
 * <p>Each session will be save to 3 different places in REDIS.
 * <ul>
 *     <li>session:id -> serialized session</li> as primary.
 *     <li>session:ip -> session id</li> to support searching.
 *     <li>session:ip:mac -> session id</li> to support searching.
 * </ul>
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Component
@Profile("cluster")
public class RedisSessionStore implements SessionStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RedisSessionStore.class);

    public static final String ID = "session:id";

    @Autowired
    @Qualifier("redisSessionTemplate")
    private RedisTemplate<String, Session> sessionRedisTemplate;

    @Autowired
    @Qualifier("redisQueryTemplate")
    private RedisTemplate<String, Long> sessionQueryRedisTemplate;

    @Autowired
    @Qualifier("redisIdTemplate")
    private RedisTemplate<String, Long> redisIdTemplate;

    String keyFor(long id) {
        return "session:" + id;
    }

    String keyFor(String ip) {
        return "session:" + ip;
    }

    String keyFor(String ip, String mac) {
        return "session:" + ip + ":" + mac;
    }

    String keyFor(Session session) {
        return keyFor(session.getId());
    }

    void ensureKey(Session session) {
        long id = redisIdTemplate.opsForValue().increment(ID, 1);
        session.setId(id);
    }

    @Override
    public void init() {
        /* Sessions only saved in REDIS, nothing to do. */
        logger.info("init, no-op");
    }

    @Override
    public long getLastUpdateTime(Long id) throws SessionNotFoundException {
        Session session = get(id);
        return session.getLastModified();
    }

    @Override
    public List<Session> find(String ip, String mac) {
        long sid = sessionQueryRedisTemplate.opsForValue().get(keyFor(ip, mac));
        Session session = sessionRedisTemplate.opsForValue().get(keyFor(sid));
        return session == null ? Collections.emptyList() : Collections.singletonList(session);
    }

    @Override
    public List<Session> find(String ip) {
        long sid = sessionQueryRedisTemplate.opsForValue().get(keyFor(ip));
        Session session = sessionRedisTemplate.opsForValue().get(keyFor(sid));
        return session == null ? Collections.emptyList() : Collections.singletonList(session);
    }

    @Override
    public Session get(Long id) throws SessionNotFoundException {
        Session session = sessionRedisTemplate.opsForValue().get(keyFor(id));
        if (session == null) {
            throw new SessionNotFoundException(id);
        }
        return session;
    }

    @Override
    public void put(Session session) {
        Objects.requireNonNull(session);
        ensureKey(session);
        Credentials credentials = session.getCredentials();
        /* session:id session values */
        sessionRedisTemplate.opsForValue().set(keyFor(session), session);
        /* session:ip: for find(ip). */
        sessionQueryRedisTemplate.opsForValue().set(keyFor(credentials.getIp()), session.getId());
        /* session:ip:mac for find(ip, mac) */
        sessionQueryRedisTemplate.opsForValue().set(
                keyFor(credentials.getIp(), credentials.getMac()), session.getId());

        if (logger.isTraceEnabled()) {
            logger.trace("session added to store, {}", session);
        }

        SessionMessage message = new SessionMessage();
        message.setType(SessionMessage.Type.ADDED);
        message.setSession(session);
        sessionRedisTemplate.convertAndSend(ClusterConfiguration.SESSION_CHANNEL, message);

        if (logger.isTraceEnabled()) {
            logger.trace("session add notified, {}", session);
        }
    }

    @Override
    public boolean exists(Long id) {
        return sessionRedisTemplate.opsForValue().get(keyFor(id)) != null;
    }

    @Override
    public void update(long id, long lastModified) throws SessionNotFoundException {
        Session session = get(id);
        session.setLastModified(lastModified);
        sessionRedisTemplate.opsForValue().set(keyFor(id), session);
    }

    @Override
    public boolean delete(Long id) throws SessionNotFoundException {
        Session session = get(id);
        Credentials credentials = session.getCredentials();
        sessionRedisTemplate.delete(keyFor(session));
        sessionQueryRedisTemplate.delete(keyFor(credentials.getIp()));
        sessionQueryRedisTemplate.delete(keyFor(credentials.getIp(), credentials.getMac()));
        boolean removed = !exists(id);
        if (logger.isTraceEnabled()) {
            logger.trace("session removed: {}, {}", id, removed);
        }

        if (removed) {
            SessionMessage message = new SessionMessage();
            message.setType(SessionMessage.Type.REMOVED);
            message.setSession(session);
            sessionRedisTemplate.convertAndSend(ClusterConfiguration.SESSION_CHANNEL, message);

            if (logger.isTraceEnabled()) {
                logger.trace("session remove notified, {}", session);
            }
        }

        return removed;
    }

    @Override
    public boolean delete(Session session) throws SessionNotFoundException {
        return delete(session.getId());
    }
}
