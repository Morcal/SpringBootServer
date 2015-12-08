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
     * @param ip user ip address.
     * @param mac user mac address.
     * @return session
     * @throws PortalException
     */
    Session createSession(String ip, String mac, String nasId) throws PortalException;

    /**
     * Get session by id.
     *
     * @param id session id.
     * @return session
     * @throws PortalException
     */
    Session getSession(long id) throws PortalException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session
     * @throws PortalException
     */
    boolean removeSession(long id) throws PortalException;
}
