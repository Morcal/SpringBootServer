package cn.com.xinli.portal.core;

import java.util.List;
import java.util.Map;

/**
 * Session store.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/29.
 */
public interface SessionStore extends DataStore<Session> {
    /**
     * Get session by id.
     *
     * @param id session id.
     * @return session
     */
    @Override
    Session get(long id) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     */
    @Override
    void update(long id, long lastModified) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     */
    @Override
    boolean delete(long id) throws SessionNotFoundException;

    /**
     * Get session last update time.
     * @param id session id.
     * @return last update time (UNIX epoch time).
     */
    long getLastUpdateTime(long id) throws SessionNotFoundException;

    /**
     * Find data with query parameters.
     * @param parameters query parameters.
     * @return result list.
     * @throws IllegalArgumentException if parameters is null or empty.
     */
    List<Session> find(Map<String, String> parameters);
}
