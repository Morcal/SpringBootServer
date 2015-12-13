package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.persist.SessionRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
public class RestSessionService implements SessionService, InitializingBean {

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionRepository);
    }

    @Override
    @Transactional
    public Session createSession(String ip, String mac, String nasId) throws PortalException {
        SessionEntity session = new SessionEntity();
        session.setIp(ip);
        session.setMac(mac);
        session.setNasId(nasId);
        session.setStartDate(Calendar.getInstance().getTime());
        session.setUsername(StringUtils.join(ip, " ", mac));

        return sessionRepository.save(session);
    }

    @Override
    @Transactional
    public Session getSession(long id) throws PortalException {
        return sessionRepository.findOne(id);
    }

    @Override
    @Transactional
    public boolean removeSession(long id) throws PortalException {
        sessionRepository.delete(id);
        return true;
    }
}
