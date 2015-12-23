package cn.com.xinli.portal;

import java.io.IOException;
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
     * Create a new portal session.
     *
     * @param session session to create.
     * @return session message.
     * @throws PortalException
     */
    Message<Session> createSession(Nas nas, Session session) throws IOException;

    /**
     * Get session by id.
     *
     * @param id session id.
     * @return session
     * @throws SessionNotFoundException
     */
    Session getSession(long id) throws SessionNotFoundException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session message.
     * @throws SessionNotFoundException
     */
    Message<Session> removeSession(long id) throws SessionNotFoundException;

    /**
     * Find session by ip and mac.
     * @param ip ip address.
     * @param mac mac address.
     * @return session if found or null.
     */
    Optional<Session> find(String ip, String mac);

    /**
     * Update session's last modified timestamp.
     * @param id session id.
     * @param timestamp last modified timestamp.
     * @return updated session.
     */
    Session update(long id, long timestamp);

    /**
     * Remove session by ip address.
     * @param ip ip address.
     */
    Message removeSession(String ip);
}
