package cn.com.xinli.portal.support.cache;

import cn.com.xinli.portal.support.ehcache.EhcacheManagerAdapter;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/16.
 */
public class EhcacheManagerAdapterTest {

    @Test
    public void testCache() {
        final String service = "cache-service", cache = "foobar";

        EhcacheManagerAdapter adapter = new EhcacheManagerAdapter(service);
        adapter.createCache(cache);

        CacheManager cacheManager = adapter.createManager();

        Assert.assertNotNull(cacheManager);

        Ehcache ehcache = cacheManager.getEhcache(cache);
        Assert.assertNotNull(ehcache);
    }
}
