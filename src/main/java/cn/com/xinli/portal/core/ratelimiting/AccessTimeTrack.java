package cn.com.xinli.portal.core.ratelimiting;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Records of REST API accesses.
 *
 * <p>This implementation should be thread-safe.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/31.
 */
public class AccessTimeTrack {

    /** Allowed access max counter. */
    private int allowed;

    /** Allowed access counter time range in milliseconds. */
    private long maxTimeDiff;

    /** Tracked records. */
    private final Queue<Long> accessTimes;

    public AccessTimeTrack(int allowed, long maxTimeDiff) {
        this.allowed = allowed;
        this.maxTimeDiff = maxTimeDiff * 1000L;
        this.accessTimes = new ConcurrentLinkedQueue<>();
    }

    /**
     * Track current access time and check if exceeds rate-limiting.
     *
     * @param timestamp current access time.
     * @return true if access is allowed.
     */
    public boolean trackAndCheckRate(long timestamp) {
        accessTimes.offer(timestamp);

        if (accessTimes.size() > allowed) {
            /* To prevent attackers send flood to cause server run
             * out of memory, keep track at maximum of 'allowed'.
             */
            while (accessTimes.size() > allowed) {
                accessTimes.poll();
            }
            return false;
        }

        Iterator<Long> iterator = accessTimes.iterator();
        while (iterator.hasNext()) {
            Long head = iterator.next();
            if ((timestamp - head) > maxTimeDiff) {
                iterator.remove();
            } else {
                /* Stop iteration. */
                break;
            }
        }
        return true;
    }
}
