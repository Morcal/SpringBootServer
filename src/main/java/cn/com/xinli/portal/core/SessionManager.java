package cn.com.xinli.portal.core;

import java.util.concurrent.Future;

/**
 * Session manager.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public interface SessionManager {
    /**
     * Create a new portal session.
     *
     * @param nas target NAS.
     * @param certificate client certificate.
     * @param credentials user's credentials.
     * @return session.
     * @throws PortalException
     */
    Session createSession(Nas nas, Certificate certificate, Credentials credentials) throws PortalException;

    /**
     * Remove session by id.
     * @param id session id.
     * @throws SessionNotFoundException
     * @throws PortalException
     */
    void removeSession(long id) throws PortalException;

    /**
     * Remove session in a future.
     * @param id session id.
     * @return future.
     */
    Future<?> removeSessionInFuture(long id);

    /**
     * Remove session by ip address.
     * @param ip ip address.
     * @throws PortalException
     */
    void removeSession(String ip) throws PortalException;
}
