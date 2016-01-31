package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.credentials.Credentials;

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
     * @param certificate client certificate.
     * @param credentials user's credentials.
     * @return session.
     * @throws PortalException
     */
    Session createSession(Certificate certificate, Credentials credentials) throws PortalException;

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
}
