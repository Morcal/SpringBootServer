package cn.com.xinli.portal;

/**
 * Data Store.
 * 
 * Project: xpws
 *
 * @param <T> data type.
 * @author zhoupeng 2015/12/29.
 */
public interface DataStore<T> {
    /**
     * Get session by id.
     *
     * @param id session id.
     * @return session
     */
    T get(long id);

    /**
     * Put data into data store.
     * @param t data.
     */
    void put(T t);

    /**
     * Check if data with id exists.
     * @param id data id.
     * @return true if exists.
     */
    boolean exists(long id);

    /**
     * Update data's last modified timestamp.
     * @param id data id.
     * @param lastModified last modified time (UNIX epoch time).
     * @return true if data updated.
     */
    boolean update(long id, long lastModified);

    /**
     * Remove.
     * @param id data id.
     * @return true if data with id deleted.
     */
    boolean delete(long id);
}