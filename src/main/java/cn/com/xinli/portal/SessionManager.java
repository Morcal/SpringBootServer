package cn.com.xinli.portal;

import cn.com.xinli.portal.protocol.Message;
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
     * @throws SessionNotFoundException
     * @throws SessionOperationException
     * @throws NasNotFoundException
     */
    Message<Session> createSession(Nas nas, Session session)
            throws SessionNotFoundException, SessionOperationException, NasNotFoundException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session message.
     * @throws SessionNotFoundException
     * @throws NasNotFoundException
     * @throws SessionOperationException
     */
    Message<Session> removeSession(long id)
            throws SessionNotFoundException, NasNotFoundException, SessionOperationException;

    Future<?> removeSessionInQueue(long id);
}
