package cn.com.xinli.portal;

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
     * @return session
     * @throws PortalException
     */
    Session createSession(Session session);

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
     * @throws SessionNotFoundException
     */
    void removeSession(long id) throws SessionNotFoundException;

    Session find(String ip, String mac);

    Session update(long id, long timestamp);
}
