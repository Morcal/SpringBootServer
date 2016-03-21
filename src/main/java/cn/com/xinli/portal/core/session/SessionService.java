package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.PortalException;

import java.util.Optional;

/**
 * Portal web server session service.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public interface SessionService {
    /**
     * Get current online session count.
     * @return current session count.
     */
    int currentSessions();
    
    /**
     * Initialize session service.
     */
    void init();

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

    /**
     * Remove session by ip address.
     *
     * <p>This method supports removing session when NAS/BRAS notified portal server
     * that certain user already disconnected.
     * @param nasIp  nas ip address.
     * @param userIp user ip address.
     * @throws PortalException
     */
    void removeSession(String nasIp, String userIp) throws PortalException;
}
