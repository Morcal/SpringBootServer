package cn.com.xinli.portal.core;

import cn.com.xinli.portal.protocol.Result;
import cn.com.xinli.portal.protocol.Nas;
import cn.com.xinli.portal.protocol.NasNotFoundException;

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
     * @return session message.
     * @throws PortalException
     * @throws NasNotFoundException
     */
    Result createSession(Nas nas, Session session)
            throws PortalException, NasNotFoundException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session message.
     * @throws SessionNotFoundException
     * @throws NasNotFoundException
     * @throws PortalException
     */
    Result removeSession(long id)
            throws PortalException, NasNotFoundException;

    /**
     * Remove session in queue.
     * @param id session id.
     * @return future.
     */
    Future<?> removeSessionInQueue(long id);
}
