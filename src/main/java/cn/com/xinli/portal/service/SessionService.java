package cn.com.xinli.portal.service;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.Session;
import cn.com.xinli.portal.core.SessionNotFoundException;

import java.util.Optional;

/**
 * Portal web server session service.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public interface SessionService {
    /**
     * Get session by id.
     *
     * @param id session id.
     * @return session
     * @throws SessionNotFoundException
     */
    Session getSession(long id) throws SessionNotFoundException;

    /**
     * Check if session with id exists.
     * @param id session id.
     * @return true if exists.
     */
    boolean exists(long id);

    /**
     * Find session by ip and mac.
     * @param ip ip address.
     * @param mac mac address.
     * @return optional of session.
     */
    Optional<Session> find(String ip, String mac);

    /**
     * Update session's last modified timestamp.
     * @param id session id.
     * @param timestamp last modified timestamp.
     * @return updated session.
     * @throws PortalException
     */
    Session update(long id, long timestamp) throws PortalException;
}
