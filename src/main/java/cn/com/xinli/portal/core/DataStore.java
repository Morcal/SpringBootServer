package cn.com.xinli.portal.core;

/**
 * Data Store.
 *
 * <p>Data store is a conceptual container which holds any numbers of
 * content objects in one place.
 * 
 * <p>Project: xpws
 *
 * @param <T> value type.
 * @param <K> key type.
 * @author zhoupeng 2015/12/29.
 */
public interface DataStore<T, K> {
    /**
     * Get session by key.
     *
     * @param key key.
     * @return session
     */
    T get(K key) throws Exception;

    /**
     * Put value into store.
     * @param value value.
     */
    void put(T value);

    /**
     * Check if data with key exists.
     * @param key key.
     * @return true if exists.
     */
    boolean exists(K key);

    /**
     * Remove.
     * @param key key.
     * @return true if value with key deleted.
     */
    boolean delete(K key) throws Exception;
}
