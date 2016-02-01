package cn.com.xinli.portal.core;

/**
 * Locatable.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface Locatable<K, V> {
    /**
     * Locate target by key.
     * @param key key.
     * @return target.
     * @throws Exception
     */
    V locate(K key) throws Exception;
}
