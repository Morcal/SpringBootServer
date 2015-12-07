package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Session;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Repository
public interface SessionDao {
    /**
     * Get session by name.
     * @param name session name.
     * @return session.
     * @throws DataAccessException if session not found.
     */
    Session get(String name) throws DataAccessException;

    /**
     * Save session.
     * @param session session to save.
     */
    void save(Session session);

    /**
     * Find session by user.
     * @param user user. It could be an ip address or
     *             an ip address and a mac address.
     * @return session if matches, or null.
     */
    Session find(String user);
}
