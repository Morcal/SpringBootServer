package cn.com.xinli.portal.support;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import cn.com.xinli.portal.protocol.*;
import cn.com.xinli.portal.protocol.huawei.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Session Service Support.
 *
 * This implementation employees {@link net.sf.ehcache.Ehcache} as {@link DataStore}
 * with the "Cache-aside" pattern.
 *
 * <ul>
 *     <li>Create session</li>
 *     Newly created session will be saved in database and be saved in
 *     the cache as well.
 *     <li>Get session</li>
 *     When system requires getting a session, this service try to retrieve it from
 *     cache first, if missed, try to load it from database and then put it
 *     into cache.
 *     <li>Update session</li>
 *     System only update session when system configuration enabled keep alive.
 *     "LastModified" attribute of {@link Session} was designed to keep trackAndCheckRate
 *     of session updating. When keep alive enabled, session cache will evict
 *     session if not been updated in a range of time elapsed, and system will
 *     logout that evicted session. It does not make more sense if we try to
 *     update session's "LastModified" in the database.
 *     <li>Delete session</li>
 *     Delete a session revolving delete a database record and a session cache.
 * </ul>
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
@Transactional(rollbackFor = { PortalException.class})
public class SessionServiceSupport implements SessionService, SessionManager, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionServiceSupport.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private NasMapping nasMapping;

    /** Remover executor. */
    private ExecutorService executor = Executors.newFixedThreadPool(
            2, r -> new Thread(r, "session-service-remover"));

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
    }

    @Override
    public boolean exists(long id) {
        if (sessionStore.exists(id)) {
            return true;
        }

        Session session = sessionRepository.findOne(id);
        if (session != null) {
            sessionStore.put(session);
            return true;
        }

        return false;
    }

    @Override
    public Future<?> removeSessionInQueue(long id) {
        return executor.submit(() -> removeQueued(id));
    }

    /**
     * Session service queued session remover.
     */
    private void removeQueued(long id) {
        try {
            removeSession(id);
        } catch (PortalException | NasNotFoundException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Session service remover error: {}", e.getMessage());
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * Try to create a portal session by communicating with target NAS.
     * If succeeded, put session into cache and save session in the database.
     *
     * @param nas target {@link Nas}.
     * @param session session to create.
     * @return message.
     * @throws SessionNotFoundException
     * @throws SessionOperationException
     * @throws NasNotFoundException
     */
    @Override
    public Message<Session> createSession(Nas nas, Session session) throws SessionNotFoundException, SessionOperationException, NasNotFoundException {
        Optional<Session> opt =
                sessionRepository.find(Session.pair(session.getIp(), session.getMac()))
                        .stream()
                        .findFirst();

        Credentials credentials = new Credentials(
                session.getUsername(), session.getPassword(), session.getIp(), session.getMac());

        try {
            if (opt.isPresent()) {
                /* Check if already existed session was created by current user.
                 * If so, return existed session, or try to logout existed user
                 * and then login with current user.
                 */
                Session existed = opt.get();

                if (logger.isTraceEnabled()) {
                    logger.trace("Session already existed, {}", existed);
                }

                if (!StringUtils.equals(existed.getUsername(), session.getUsername())) {
                    logger.warn("+ session already exists with different username.");
                    Credentials old = new Credentials(
                            existed.getUsername(), existed.getPassword(), existed.getIp(), existed.getMac());
                    PortalClient client = HuaweiPortal.createClient(nas);
                    Message<?> message = client.logout(old);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Create session, logout already existed, portal result: {}",
                                message.getText());
                    }

                    if (!message.isSuccess()) {
                        logger.warn("+ logout existed different user failed, cause: {}.",
                                message.getText());
                    }

                    removeSession(existed.getId());
                } else {
                    return Message.of(existed, true, "Session already exists.");
                }
            }

            PortalClient client = HuaweiPortal.createClient(nas);
            Message<?> message = client.login(credentials);
            if (logger.isDebugEnabled()) {
                logger.debug("Portal login result: {}", message);
            }

            if (message.isSuccess()) {
                sessionRepository.save((SessionEntity) session);
                /*
                 * Only put to cache if no exceptions or rollback occurred.
                 */
                sessionStore.put(session);
            }

            return Message.of(session, message.isSuccess(), message.getText());
        } catch (IOException | PortalProtocolException e) {
            throw new SessionOperationException("Create session error", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Try to retrieve session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store, or else
     * throw {@link SessionNotFoundException}. ("Cache-aside" pattern).
     * @param id session id.
     * @return session found.
     * @throws SessionNotFoundException if session not found.
     */
    @Override
    public Session getSession(long id) throws SessionNotFoundException {
        Session found = sessionStore.get(id);

        if (found == null) {
            found = sessionRepository.findOne(id);
            if (found == null) {
                throw new SessionNotFoundException(id);
            }
        }

        sessionStore.put(found);
        return found;
    }

    /**
     * Remove session internal.
     * @param session session to remove.
     * @return message.
     * @throws NasNotFoundException
     * @throws SessionOperationException
     */
    private Message<Session> removeSessionInternal(Session session) throws NasNotFoundException, SessionOperationException {
        Credentials credentials = new Credentials(
                session.getUsername(), session.getPassword(), session.getIp(), session.getMac());

        Optional<Nas> nas = nasMapping.getNas(session.getNasId());
        nas.orElseThrow(() -> new NasNotFoundException("NAS not found by id: " + session.getNasId()));

        try {
            PortalClient client = HuaweiPortal.createClient(nas.get());
            Message<?> message = client.logout(credentials);
            if (logger.isDebugEnabled()) {
                logger.debug("Portal logout result: {}", message);
            }

            long id = session.getId();
            if (message.isSuccess()) {
                if (!sessionStore.delete(id)) {
                    logger.error("* Removed session {} from cache failed.", id);
                }
                sessionRepository.delete(id);
                logger.debug("Session {} removed.", id);
            }
            return Message.of(session, message.isSuccess(), message.getText());
        } catch (IOException | PortalProtocolException e) {
            logger.error("Portal logout error", e);
            throw new SessionOperationException("Failed to logout", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Try to remove session by communicating with target {@link Nas}.
     * if succeeded, remove session from data store and database.
     *
     * @param id session id.
     * @return message.
     * @throws SessionNotFoundException
     * @throws NasNotFoundException
     * @throws SessionOperationException
     */
    @Override
    public Message<Session> removeSession(long id)
            throws SessionNotFoundException, NasNotFoundException, SessionOperationException {
        Session session = sessionRepository.findOne(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }

        return removeSessionInternal(session);
    }

    /**
     * {@inheritDoc}
     *
     * Try to find session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store, or else
     * throw {@link SessionNotFoundException}. ("Cache-aside" pattern).
     * @param ip user ip address.
     * @param mac user mac address.
     * @return result.
     */
    @Override
    public Optional<Session> find(String ip, String mac) {
        Map<String, String> values = new HashMap<>();
        values.put("ip", ip);
        values.put("mac", mac);
        List<Session> found = sessionStore.find(values);
        if (!found.isEmpty()) {
            return Optional.of(found.get(0));
        } else {
            found = sessionRepository.find(ip, mac);
            found.forEach(session -> sessionStore.put(session));
            return found.isEmpty() ? Optional.empty() : Optional.of(found.get(0));
        }
    }

    /**
     * {@inheritDoc}
     *
     * Only sessions in cache will be updated. It does not make more sense
     * to update sessions in database, since system only evict sessions in
     * the cache.
     *
     * @param id session id.
     * @param timestamp last modified timestamp.
     * @return updated session if success.
     * @throws SessionNotFoundException
     * @throws InvalidPortalRequestException
     */
    @Override
    public Session update(long id, long timestamp) throws SessionNotFoundException, InvalidPortalRequestException {
        long lastUpdateTime = sessionStore.getLastUpdateTime(id);
        if (lastUpdateTime == -1L) {
            throw new SessionNotFoundException(id);
        }

        logger.trace("session last update time: {}, current update time: {}",
                lastUpdateTime, timestamp);

        if (Math.abs(lastUpdateTime - timestamp) <= SecurityConfiguration.MIN_TIME_UPDATE_DIFF) {
            /* Assume it's a replay attack. */
            throw new InvalidPortalRequestException("Update within an invalid range.");
        }

        if (sessionStore.update(id, timestamp)) {
            return sessionStore.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Message removeSession(String ip)
            throws SessionNotFoundException, SessionOperationException, NasNotFoundException {
        Optional<Session> found = sessionRepository.find(ip)
                .stream().findFirst();

        found.orElseThrow(() -> new SessionNotFoundException("session with ip: " + ip + " not found."));

        return removeSessionInternal(found.get());
    }
}
