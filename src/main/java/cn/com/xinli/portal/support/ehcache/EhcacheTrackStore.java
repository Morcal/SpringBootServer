package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.core.ratelimiting.AccessTimeTrack;
import cn.com.xinli.portal.core.ratelimiting.TrackStore;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Rate-Limiting track store based on <a href="http://ehcache.org">EhCache</a>.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
@Profile("standalone")
public class EhcacheTrackStore implements TrackStore {

    @Autowired
    private Ehcache rateLimitingCache;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    void doPut(String remote, AccessTimeTrack track) {
        Element element = new Element(remote, track, 1, 1);
        rateLimitingCache.put(element);
    }

    @Override
    public AccessTimeTrack get(String remote) {
        Element element = rateLimitingCache.get(remote);
        return element == null ? null :(AccessTimeTrack) element.getObjectValue();
    }

    @Override
    public void put(String remote) {
        long now = System.currentTimeMillis();
        /* EhCache get/put operations are thread-safe. */
        AccessTimeTrack track = new AccessTimeTrack(
                serverConfigurationService.getServerConfiguration().getRateLimitingConfiguration().getRate(), 1L);
        track.trackAndCheckRate(now);

        doPut(remote, track);
    }

    @Override
    public void put(String remote, AccessTimeTrack track) {
        doPut(remote, track);
    }
}
