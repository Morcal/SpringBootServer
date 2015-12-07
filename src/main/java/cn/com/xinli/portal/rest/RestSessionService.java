package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.User;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
public class RestSessionService implements SessionService {
    @Override
    public Session createSession(User user) throws PortalException {
        return null;
    }

    @Override
    public Session getSession(String sessionId) throws PortalException {
        return null;
    }

    @Override
    public boolean removeSession(String sessionId) throws PortalException {
        return false;
    }
}
