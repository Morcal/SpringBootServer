package cn.com.xinli.portal;

import java.io.IOException;

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
     */
    Message<Session> createSession(Nas nas, Session session) throws IOException;

    /**
     * Remove session by id.
     * @param id session id.
     * @return session message.
     * @throws SessionNotFoundException
     */
    Message<Session> removeSession(long id) throws SessionNotFoundException;
}
