package cn.com.xinli.portal.support;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.PortalClient;
import org.apache.commons.lang3.StringUtils;
import cn.com.xinli.portal.protocol.support.PortalClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Calendar;
import java.util.Optional;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
public class SessionServiceSupport implements SessionService, SessionManager, InitializingBean {
    /** Log. */
    private static final Log log = LogFactory.getLog(SessionServiceSupport.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private NasMapping nasMapping;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
    }

    @Override
    public Message<Session> createSession(Nas nas, Session session) throws IOException {
        Optional<Session> opt =
                sessionRepository.find(Session.pair(session.getIp(), session.getMac()))
                        .stream()
                        .findFirst();

        Credentials credentials = new Credentials(
                session.getUsername(), session.getPassword(), session.getIp(), session.getMac());

        if (opt.isPresent()) {
            /* Check if already existed session was created by current user.
             * If so, return existed session, or try to logout existed user
             * and then login with current user.
             */
            Session existed = opt.get();

            if (!StringUtils.equals(existed.getUsername(), session.getUsername())) {
                log.warn("+ session already exists with different username.");
                Credentials old = new Credentials(
                        existed.getUsername(), existed.getPassword(), existed.getIp(), existed.getMac());
                PortalClient client = PortalClients.create(nas);
                Message<?> message = client.logout(old);
                if (log.isDebugEnabled()) {
                    log.debug(message);
                }

                if (!message.isSuccess()) {
                    log.warn("+ logout existed different user failed, cause: " + message.getText());
                }

                removeSession(existed.getId());
            } else {
                return Message.of(opt.get(), true, "Session already exists.");
            }
        }

        PortalClient client = PortalClients.create(nas);
        Message<?> message = client.login(credentials);
        if (log.isDebugEnabled()) {
            log.debug(message);
        }

        if (message.isSuccess()) {
            sessionRepository.save((SessionEntity) session);
        }

        return Message.of(session, message.isSuccess(), message.getText());

    }

    @Override
    @Transactional
    public Session getSession(long id) throws SessionNotFoundException {
        Session found = sessionRepository.findOne(id);
        if (found == null) {
            throw new SessionNotFoundException(id);
        }
        return found;
    }

    @Override
    @Transactional
    public Message<Session> removeSession(long id) throws SessionNotFoundException {
        Session session = sessionRepository.findOne(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }

        Credentials credentials = new Credentials(
                session.getUsername(), session.getPassword(), session.getIp(), session.getMac());

        Optional<Nas> nas = nasMapping.getNas(session.getNasId());
        nas.orElseThrow(() -> new NasNotFoundException("NAS not found by id: " + session.getNasId()));

        try {
            PortalClient client = PortalClients.create(nas.get());
            Message<?> message = client.logout(credentials);
            if (log.isDebugEnabled()) {
                log.debug(message);
            }

            if (message.isSuccess()) {
                sessionRepository.delete(id);
            }
            return Message.of(session, message.isSuccess(), message.getText());
        } catch (IOException e) {
            log.error(e);
            throw new SessionOperationException("Failed to logout", e);
        }
    }

    @Override
    @Transactional
    public Optional<Session> find(String ip, String mac) {
        return sessionRepository.find(Session.pair(ip, mac)).stream().findFirst();
    }

    @Override
    @Transactional
    public Session update(long id, long timestamp) {
        SessionEntity found = sessionRepository.findOne(id);
        if (found == null) {
            throw new SessionNotFoundException(id);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000L);
        found.setLastModified(calendar.getTime());

        return sessionRepository.save(found);
    }

    @Override
    @Transactional
    public Message removeSession(String ip) {
        Session found = sessionRepository.find1(ip);
        if (found == null) {
            throw new SessionNotFoundException("session with ip: " + ip + " not found.");
        }

        return removeSession(found.getId());
    }
}
