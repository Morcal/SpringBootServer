package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.User;
import cn.com.xinli.portal.persist.SessionDao;
import cn.com.xinli.portal.persist.SessionRepository;
import cn.com.xinli.portal.util.AbstractSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Calendar;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Service
public class RestSessionService extends AbstractSessionService {

    @Override
    public Session createSession(String ip, String mac, String nasId) throws PortalException {
        Session session = new Session();
        session.setIp(ip);
        session.setMac(mac);
        session.setNasId(nasId);
        session.setStartDate(Calendar.getInstance().getTime());

        return getSessionRepository().save(session);
    }

    @Override
    public Session getSession(String id) throws PortalException {
        return getSessionRepository().findOne(id);
    }

    @Override
    public boolean removeSession(String id) throws PortalException {
        getSessionRepository().delete(id);
        return true;
    }
}
