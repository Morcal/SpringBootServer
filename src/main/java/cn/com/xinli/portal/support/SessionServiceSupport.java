package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
    }

    @Override
    public Session createSession(Session session) {
        Optional<Session> opt = sessionRepository.find(Session.pair(session.getIp(), session.getMac()))
                .stream()
                .findFirst();

        if (opt.isPresent()) {
            log.warn("> session already exists.");
            return opt.get();
        } else {
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
        if (sessionRepository.findOne(id) == null) {
            throw new SessionNotFoundException(id);
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
}
