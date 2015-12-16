package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
public class RestSessionService implements SessionService, InitializingBean {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestSessionService.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
    }

    @Override
    public Session createSession(Session session) {
        SessionEntity existed = sessionRepository.find(Session.pair(session.getIp(), session.getMac()));
        if (existed != null) {
            log.warn("> session already exists." + existed);
            return existed;
        }

        SessionEntity entity = (SessionEntity) session;
        return sessionRepository.save(entity);
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
    public Session find(String ip, String mac) {
       return sessionRepository.find(Session.pair(ip, mac));
    }

    @Override
    @Transactional
    public Session update(long id, long timestamp) {
        SessionEntity found = sessionRepository.findOne(id);
        if (found != null) {
            throw new SessionNotFoundException(id);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        Date date = calendar.getTime();

        found.setLastModified(date);

        return sessionRepository.save(found);
    }
}
