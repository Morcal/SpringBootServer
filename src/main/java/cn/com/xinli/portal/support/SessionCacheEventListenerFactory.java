package cn.com.xinli.portal.support;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
public class SessionCacheEventListenerFactory extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new SessionCacheEventListener();
    }
}
