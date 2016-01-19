package cn.com.xinli.portal.core;

import cn.com.xinli.portal.protocol.Nas;
import cn.com.xinli.portal.protocol.Result;

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
     * @param session session to create.
     * @return session result.
     * @throws PortalException
     */
    Result createSession(Nas nas, Session session) throws PortalException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session result.
     * @throws SessionNotFoundException
     * @throws PortalException
     */
    Result removeSession(long id) throws PortalException;

    /**
     * Remove session in a future.
     * @param id session id.
     * @return future.
     */
    Future<?> removeSessionInFuture(long id);
}
