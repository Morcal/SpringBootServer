package cn.com.xinli.portal.core;

import java.util.List;
import java.util.Map;

/**
 * Session store.
 *
 * <p>Data store is a conceptual container which holds any numbers of
 * {@link Session}s in one place.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/29.
 */
public interface SessionStore extends DataStore<Session> {
    /**
     * {@inheritDoc}
     *
     * Get session last update time.
     * @param id session id.
     * @return last update time (UNIX epoch time).
     * @throws SessionNotFoundException
     */
    long getLastUpdateTime(long id) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     *
     * Find data with query parameters.
     * @param parameters query parameters.
     * @return result list.
     * @throws IllegalArgumentException if parameters is null or empty.
     */
    List<Session> find(Map<String, String> parameters);

    /**
     * {@inheritDoc}
     *
     * Get session by id.
     *
     * @param id session id.
     * @return session
     * @throws SessionNotFoundException
     */
    @Override
    Session get(long id) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     *
     * @param id session id.
     * @param lastModified last modified UNIX time.
     * @throws SessionNotFoundException
     */
    @Override
    void update(long id, long lastModified) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     * @param id session id.
     * @throws SessionNotFoundException
     */
    @Override
    boolean delete(long id) throws SessionNotFoundException;
}
