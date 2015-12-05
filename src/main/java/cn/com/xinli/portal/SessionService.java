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
     * @param user user.
     * @return session
     * @throws PortalException
     */
    Session createSession(User user) throws PortalException;

    /**
     * Get session by id.
     *
     * @param sessionId session id.
     * @return session
     * @throws PortalException
     */
    Session getSession(String sessionId) throws PortalException;

    /**
     * Remove session by id.
     * @param sessionId session id.
     * @return session
     * @throws PortalException
     */
    boolean removeSession(String sessionId) throws PortalException;
}
