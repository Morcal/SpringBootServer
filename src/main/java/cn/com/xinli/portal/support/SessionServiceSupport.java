package cn.com.xinli.portal.support;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.PortalClient;
import cn.com.xinli.portal.protocol.PortalClients;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
public class SessionServiceSupport implements SessionService, InitializingBean {
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
    public Session createSession(Nas nas, Session session) throws IOException {
        Optional<Session> opt =
                sessionRepository.find(Session.pair(session.getIp(), session.getMac()))
                        .stream()
                        .findFirst();

        if (opt.isPresent()) {
            log.warn("> session already exists.");
            return opt.get();
        } else {
            Credentials credentials = new Credentials(
                    session.getUsername(), session.getPassword(), session.getIp(), session.getMac());
            PortalClient client = PortalClients.create(nas);
            client.login(credentials);
            return sessionRepository.save((SessionEntity) session);
        }
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
    public void removeSession(long id) throws SessionNotFoundException {
        Session session = sessionRepository.findOne(id);
        if (session == null) {
            throw new SessionNotFoundException(id);
        }

        Credentials credentials = new Credentials(
                session.getUsername(), session.getPassword(), session.getIp(), session.getMac());

        Nas nas = nasMapping.getNas(session.getNasId());
        if (nas == null) {
            throw new NasNotFoundException("NAS not found by id: " + session.getNasId());
        }

        try {
            PortalClient client = PortalClients.create(nas);
            client.logout(credentials);
        } catch (IOException e) {
            log.error(e);
            throw new SessionOperationException("Failed to logout", e);
        }

        sessionRepository.delete(id);
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

        found.setLastModified(new Date(timestamp));

        return sessionRepository.save(found);
    }

    @Override
    @Transactional
    public void removeSession(String ip) {
        Session found = sessionRepository.find1(ip);
        if (found == null) {
            throw new SessionNotFoundException("session with ip: " + ip + " not found.");
        }

        removeSession(found.getId());
    }
}
