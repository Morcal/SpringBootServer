package cn.com.xinli.portal.support.ehcache;

import java.util.Collection;

/**
 * EhCache searchable configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/3.
 */
public interface EhcacheSearchable<T> {
    /**
     * Get search value.
     * @return search value.
     */
    T getValue();

    /**
     * Get EhCache search attributes.
     * @return EhCache search attributes.
     */
    Collection<EhcacheSearchAttribute> getSearchAttributes();
}
