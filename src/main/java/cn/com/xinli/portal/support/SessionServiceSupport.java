package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.RemoteException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasLocator;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasService;
import cn.com.xinli.portal.core.session.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.*;

/**
 * Session Service Support.
 *
 * <p>This class provides session operations.
 * <ul>
 *     <li>Create session</li>
 *     Newly created session should be saved in database and be saved in
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
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
@Transactional(rollbackFor = { PortalException.class, DataAccessException.class })
public class SessionServiceSupport implements SessionService, SessionManager, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionServiceSupport.class);

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private NasService nasService;

    @Autowired
    private NasLocator nasLocator;

    @Autowired
    private List<SessionProvider> sessionProviders;

    /** Session update minimum interval. */
    private int updateMinInterval;

    /** Remove queue. */
    private final BlockingQueue<Session> removeQueue;

    @Autowired
    public SessionServiceSupport(ServerConfiguration serverConfiguration) {
        updateMinInterval = serverConfiguration.getSessionConfiguration().getMinUpdateInterval();
        removeQueue = new ArrayBlockingQueue<>(
                serverConfiguration.getSessionConfiguration().getRemoveQueueMaxLength());
        /* Remover executor. */
        ExecutorService executor = Executors.newFixedThreadPool(
                2,
                r -> new Thread(r, "session-service-remover"));
        executor.submit((Runnable) this::removeSessionFromQueue);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionProviders);
    }

    @Override
    public boolean exists(long id) {
        return sessionStore.exists(id);
    }

    /**
     * Remove session from queue.
     */
    private void removeSessionFromQueue() {
        logger.info("starting session removing task.");
        while (true) {
            try {
                Session session = removeQueue.take();
                Objects.requireNonNull(session);
                removeSessionInternal(session);
            } catch (PortalException e) {
                logger.info("remove session error, {}", e.getMessage());
            } catch (InterruptedException e) {
                logger.info("Session service interrupted.");
                break;
            }
        }
    }

    @Override
    public boolean removeSessionInFuture(Session session) {
        boolean ret = removeQueue.offer(session);
        if (!ret) {
            logger.error("failed to add session remove queue, {}", session);
        }
        return ret;
    }

    @Override
    public void init() {
        logger.info("Initializing session service.");
        sessionStore.init();
    }

    /**
     * Find session provider for nas.
     * @param nas nas.
     * @return session provider.
     * @throws ServerException
     */
    private SessionProvider find(Nas nas) throws ServerException {
        for (SessionProvider provider : sessionProviders) {
            if (provider.supports(nas)) {
                return provider;
            }
        }
        throw new ServerException(PortalError.UNSUPPORTED_NAS, "not supported nas");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to create a portal session by communicating with target NAS.
     * If succeeded, put session into cache and save session in the database.
     *
     * @param certificate client certificate.
     * @param credentials user's credentials.
     * @return result.
     * @throws NasNotFoundException
     */
    @Override
    public Session createSession(Certificate certificate, Credentials credentials) throws PortalException {
        String ip = credentials.getIp(), mac = credentials.getMac();

        Set<Session> exists;
        if (StringUtils.isEmpty(mac)) {
            /* MAC is missing, may be authenticating via web page. */
            exists = sessionStore.find(ip);
        } else {
            exists = sessionStore.find(ip, mac);
        }

        Optional<Session> existed = exists.stream().findFirst();
        if (existed.isPresent()) {
            /* Check if already existed session was created by current user.
             * If so, return existed session, or else try to logout existed user
             * and then login with current user.
             */
            if (logger.isDebugEnabled()) {
                logger.info("Session already existed, {}", existed);
            }

            if (!StringUtils.equals(
                    existed.get().getCredentials().getUsername(), credentials.getUsername())) {
                logger.warn("+ session already exists with different username.");
                removeSessionInternal(existed.get());
            } else {
                return existed.get();
            }
        }

        Nas nas = nasLocator.locate(credentials);
        CredentialsTranslation translation = nas.getTranslation();
        if (translation != null) {
            credentials = translation.translate(credentials);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("authenticating: {}", credentials);
        }
        SessionProvider provider = find(nas);
        Session session = provider.authenticate(nas, credentials);

        /* Populate certificate. */
        session.setStartTime(Calendar.getInstance().getTime());
        session.setCertificate(certificate);
        /* Only put to cache if no exceptions or rollback occurred. */
        sessionStore.put(session);
        return session;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to retrieve session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store, or else
     * throw {@link SessionNotFoundException}. ("Cache-aside" pattern).
     *
     * @param id session id.
     * @return session found.
     * @throws SessionNotFoundException if session not found.
     */
    @Override
    public Session getSession(long id) throws SessionNotFoundException {
        return sessionStore.get(id);
    }

    /**
     * Remove session internal.
     *
     * @param session session to remove.
     * @throws PortalException
     */
    private void removeSessionInternal(Session session) throws PortalException {
        Nas nas = nasService.find(session.getNas().getName());
        long id = session.getId();

        SessionProvider provider = find(nas);
        provider.disconnect(session);

        /* Only put to cache if no exceptions or rollback occurred. */
        sessionStore.delete(id);
        logger.debug("Session {} removed.", id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to remove session by communicating with target {@link Nas}.
     * if succeeded, remove session from data store and database.
     *
     * @param id session id.
     * @return result.
     * @throws PortalException
     * @throws NasNotFoundException
     */
    @Override
    public void removeSession(long id) throws PortalException {
        Session session = sessionStore.get(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }

        removeSessionInternal(session);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Try to find session from data store first, if not in there,
     * then try to load from database.
     * If session loaded from database, put it into data store, or else
     * throw {@link SessionNotFoundException}. ("Cache-aside" pattern).
     *
     * @param ip user ip address.
     * @param mac user mac address.
     * @return result.
     */
    @Override
    public Optional<Session> find(String ip, String mac) {
        Set<Session> sessions = sessionStore.find(ip, mac);
        return sessions.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only sessions in cache will be updated. It does not make more sense
     * to update sessions in database, since system only evict sessions in
     * the cache.
     *
     * @param id session id.
     * @param timestamp last modified timestamp.
     * @return updated session if success.
     * @throws PortalException
     */
    @Override
    public Session update(long id, long timestamp) throws PortalException {
        long lastUpdateTime = sessionStore.getLastUpdateTime(id);
        if (lastUpdateTime == -1L) {
            throw new SessionNotFoundException(id);
        }

        logger.trace("session last update time: {}, current update time: {}",
                lastUpdateTime, timestamp);

        if (Math.abs(lastUpdateTime - timestamp) <= updateMinInterval) {
            /* Assume it's a replay attack. */
            throw new RemoteException(PortalError.INVALID_UPDATE_TIMESTAMP);
        }

        sessionStore.update(id, timestamp);
        return sessionStore.get(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delete session without logout. This method does not thrown any
     * checked exceptions.
     *
     * @param nasIp nas ip.
     * @param userIp user ip.
     * @throws PortalException
     */
    @Override
    public void removeSession(String nasIp, String userIp) throws PortalException {
        Set<Session> found = sessionStore.find(userIp);
        Optional<Session> session = found.stream().findFirst();
        if (session.isPresent()) {
            sessionStore.delete(session.get().getId());
        }
    }
}
