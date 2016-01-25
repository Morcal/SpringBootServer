package cn.com.xinli.portal.service;

import cn.com.xinli.portal.configuration.SecurityConfiguration;
import cn.com.xinli.portal.core.*;
import cn.com.xinli.portal.repository.SessionRepository;
import cn.com.xinli.portal.support.PortalErrorTranslator;
import cn.com.xinli.portal.transport.PortalClient;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.ProtocolError;
import cn.com.xinli.portal.transport.Result;
import cn.com.xinli.portal.transport.huawei.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Session Service Support.
 *
 * <p>This implementation employees {@link net.sf.ehcache.Ehcache} as {@link DataStore}
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
 *
 * <p>This implement is based on a fact that Huawei portal protocol logout requests
 * only require user's ip address. So, when portal web server try to logout
 * certain user, user's authentication information includes account name and user's
 * password may be left out. As result, logout user's credentials may can not pass
 * credentials integrity validation.
 *
 * <p>The {@link Session} class was designed to support Huawei based portal protocol,
 * and it does not contains full user's credentials. If protocol (such as other protocol
 * providers) requires full user's credentials, then full user's credentials need
 * be accessible through {@link Session}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
@Transactional(rollbackFor = { PortalException.class, DataAccessException.class})
public class SessionServiceSupport implements SessionService, SessionManager, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SessionServiceSupport.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionStore sessionStore;

    @Autowired
    private NasService nasService;

    @Autowired
    private PortalErrorTranslator errorTranslator;

    @Value("${pws.cluster.enable}") private boolean clusterEnabled;

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
    public Future<?> removeSessionInFuture(long id) {
        return executor.submit(() -> doRemoveSession(id));
    }

    /**
     * Session service queued session remover.
     */
    private void doRemoveSession(long id) {
        try {
            removeSession(id);
        } catch (PortalException e) {
            if (logger.isTraceEnabled()) {
                logger.trace("Session service remover error: {}", e.getMessage());
            }
        }
    }

    /**
     * Build a session with nas, certificate and credentials.
     * @param nas target nas.
     * @param certificate client certificate.
     * @param credentials credentials.
     * @return session.
     */
    private DbSession buildDbSession(Nas nas, Certificate certificate, Credentials credentials) {
        DbSession session = new DbSession();
        session.setIp(credentials.getIp());
        session.setMac(credentials.getMac());
        session.setUsername(credentials.getUsername());
        session.setCertificate(certificate);
        session.setNas(nas);
        session.setStartTime(Calendar.getInstance().getTime());
        return session;
    }

    /**
     * {@inheritDoc}
     *
     * Try to create a portal session by communicating with target NAS.
     * If succeeded, put session into cache and save session in the database.
     *
     * @param nas target {@link Nas}.
     * @param certificate client certificate.
     * @param credentials user's credentials.
     * @return result.
     * @throws NasNotFoundException
     */
    @Override
    public Session createSession(Nas nas, Certificate certificate, Credentials credentials) throws PortalException {
        Optional<Session> opt =
                sessionRepository.find(Session.pair(credentials.getIp(), credentials.getMac()))
                        .stream()
                        .findFirst();
        try {
            if (opt.isPresent()) {
                /* Check if already existed session was created by current user.
                 * If so, return existed session, or else try to logout existed user
                 * and then login with current user.
                 */
                Session existed = opt.get();

                logger.info("Session already existed, {}", existed);

                if (!StringUtils.equals(existed.getUsername(), credentials.getUsername())) {
                    logger.warn("+ session already exists with different username.");
                    removeSessionInternal(existed);
                } else {
                    return existed;
                }
            }

            PortalClient client = HuaweiPortal.createClient(nas);
            Result result = client.login(credentials);

            if (logger.isDebugEnabled()) {
                logger.debug("Portal login result: {}", result);
            }
        } catch (IOException e) {
            logger.error("Portal login error", e);
            throw new ServerException(PortalError.IO_ERROR, "Failed to login", e);
        } catch (PortalProtocolException e) {
            /*
             * Wrap protocol exception into a new platform exception,
             * unless, 1. login CHAP-challenge when already online,
             * 2. login CHAP-authenticate when already online.
             */
            ProtocolError error = e.getProtocolError();
            if (error != ProtocolError.CHALLENGE_ALREADY_ONLINE &&
                    error != ProtocolError.AUTHENTICATION_ALREADY_ONLINE) {
                PortalError err = errorTranslator.translate(e);
                throw new PlatformException(err, e.getMessage(), e);
            }
        }

        DbSession session = buildDbSession(nas, certificate, credentials);
        sessionRepository.save(session);
        /* Only put to cache if no exceptions or rollback occurred. */
        sessionStore.put(session);

        return session;
    }

    /**
     * {@inheritDoc}
     *
     * Try to retrieve session from data store first, if not in there,
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
        Session cached = sessionStore.get(id);

        if (cached == null) {
            if (!clusterEnabled) {
                Session session = sessionRepository.findOne(id);
                if (session != null) {
                    sessionStore.put(session);
                    return session;
                }
            }
            throw new SessionNotFoundException(id);
        } else {
            return cached;
        }
    }

    /**
     * Remove session internal.
     *
     * @param session session to remove.
     * @throws PortalException
     */
    private void removeSessionInternal(Session session) throws PortalException {
        Credentials credentials = new Credentials(null, null, session.getIp(), null);

        Nas nas = nasService.find(session.getNasName());

        try {
            PortalClient client = HuaweiPortal.createClient(nas);
            Result result = client.logout(credentials);

            if (logger.isDebugEnabled()) {
                logger.debug("Portal logout result: {}", result);
            }
        } catch (IOException e) {
            logger.error("Portal logout error", e);
            throw new ServerException(
                    PortalError.IO_ERROR, "Failed to logout", e);
        } catch (PortalProtocolException e) {
            /*
             * Wrap protocol exception into a new Platform exception
             * unless trying to logout when user already gone.
             */
            ProtocolError error = e.getProtocolError();
            if (error != ProtocolError.LOGOUT_ALREADY_GONE) {
                PortalError err = errorTranslator.translate(e);
                throw new PlatformException(err, e.getMessage(), e);
            }
        }

        long id = session.getId();

        if (!sessionStore.delete(id)) {
            logger.error("* Removed session {} from cache failed.", id);
        }
        sessionRepository.delete(id);
        logger.debug("Session {} removed.", id);
    }

    /**
     * {@inheritDoc}
     *
     * Try to remove session by communicating with target {@link Nas}.
     * if succeeded, remove session from data store and database.
     *
     * @param id session id.
     * @return result.
     * @throws PortalException
     * @throws NasNotFoundException
     */
    @Override
    public void removeSession(long id) throws PortalException {
        Session session = sessionRepository.findOne(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }

        removeSessionInternal(session);
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

        if (Math.abs(lastUpdateTime - timestamp) <= SecurityConfiguration.MIN_TIME_UPDATE_DIFF) {
            /* Assume it's a replay attack. */
            throw new RemoteException(PortalError.INVALID_UPDATE_TIMESTAMP);
        }

        sessionStore.update(id, timestamp);
        return sessionStore.get(id);
    }

    @Override
    public void removeSession(String ip) throws PortalException {
        Optional<Session> found = sessionRepository.find(ip)
                .stream().findFirst();

        found.orElseThrow(() ->
                new SessionNotFoundException("ip: " + ip));

        removeSessionInternal(found.get());
    }
}
