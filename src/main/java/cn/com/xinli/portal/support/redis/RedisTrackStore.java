package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.ratelimiting.AccessTimeTrack;
import cn.com.xinli.portal.core.ratelimiting.TrackStore;
import cn.com.xinli.portal.web.configuration.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * REDIS rate-limiting track store.
 *
 * <p>This class saves rate-limiting access track in REDIS with
 * key rate-limiting:remote ip.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
@Profile("cluster")
public class RedisTrackStore implements TrackStore {
    @Autowired
    private RedisTemplate<String, AccessTimeTrack> redisTrackTemplate;

    @Override
    public AccessTimeTrack get(String remote) {
        return redisTrackTemplate.opsForValue().get(remote);
    }

    String keyFor(String remote) {
        return "rate-limiting:" + remote;
    }

    void doPut(String remote, AccessTimeTrack track) {
        redisTrackTemplate.opsForValue().set(keyFor(remote), track);
        redisTrackTemplate.expire(remote, 1L, TimeUnit.SECONDS);
    }

    @Override
    public void put(String remote) {
        AccessTimeTrack track = new AccessTimeTrack(SecurityConfiguration.RATE_LIMITING, 1L);
        doPut(remote, track);
    }

    @Override
    public void put(String remote, AccessTimeTrack track) {
        doPut(keyFor(remote), track);
    }
}