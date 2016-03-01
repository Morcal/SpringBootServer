package cn.com.xinli.portal.core.ratelimiting;

/**
 * Rate-Limiting access time track store.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
public interface TrackStore {
    AccessTimeTrack get(String remote);

    void put(String remote);
    void put(String remote, AccessTimeTrack track);
}
