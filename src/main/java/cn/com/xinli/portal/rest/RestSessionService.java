package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.util.AbstractSessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
        SessionEntity session = new SessionEntity();
        session.setIp(ip);
        session.setMac(mac);
        session.setNasId(nasId);
        session.setStartDate(Calendar.getInstance().getTime());
        session.setUsername(StringUtils.join(ip, " ", mac));

        return getSessionRepository().save(session);
    }

    @Override
    public Session getSession(long id) throws PortalException {
        return getSessionRepository().findOne(id);
    }

    @Override
    public boolean removeSession(long id) throws PortalException {
        getSessionRepository().delete(id);
        return true;
    }
}
