package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.DataStore;

import java.util.Set;
import java.util.stream.Stream;

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
public interface SessionStore extends DataStore<Session, Long> {
    void init();

    /**
     * {@inheritDoc}
     *
     * Get session last update time.
     * @param id session id.
     * @return last update time (UNIX epoch time).
     * @throws SessionNotFoundException
     */
    long getLastUpdateTime(Long id) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     *
     * Find data with query parameters.
     * @param ip user ip address.
     * @param mac user mac address.
     * @return results.
     * @throws IllegalArgumentException if parameters is null or empty.
     */
    Set<Session> find(String ip, String mac);

    /**
     * {@inheritDoc}
     *
     * Find data with query parameters.
     * @param ip user ip address.
     * @return results.
     * @throws IllegalArgumentException if parameters is null or empty.
     */
    Set<Session> find(String ip);

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
    Session get(Long id) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     *
     * @param id session id.
     * @param lastModified last modified UNIX time.
     * @throws SessionNotFoundException
     */
    void update(long id, long lastModified) throws SessionNotFoundException;

    /**
     * {@inheritDoc}
     * @param id session id.
     * @throws SessionNotFoundException
     */
    @Override
    boolean delete(Long id) throws SessionNotFoundException;

    boolean delete(Session session) throws SessionNotFoundException;

    /**
     * Count all sessions.
     * @return session count.
     */
    long count();

    /**
     * Get all sessions.
     * @return stream of existed sessions.
     */
    Stream<Session> all();

    /**
     * Search sessions.
     * @param query search keyword.
     * @return stream of resulting sessions.
     */
    Stream<Session> search(String query);

    /**
     * Count sessions by keyword.
     * @param query search keyword.
     * @return resulting count.
     */
    long count(String query);
}
