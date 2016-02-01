package cn.com.xinli.portal.core.radius;

import cn.com.xinli.portal.core.DataStore;

/**
 * Radius server store.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public interface RadiusStore extends DataStore<Radius, Long> {
    @Override
    Radius get(Long id) throws RadiusNotFoundException;

    @Override
    boolean delete(Long id) throws RadiusNotFoundException;
}
