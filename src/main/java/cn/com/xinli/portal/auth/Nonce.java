package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.util.TimeLimited;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/5.
 */
public class Nonce implements TimeLimited {
    /** Sequence. */
    private static AtomicLong sequence = new AtomicLong(0);

    private final long id;

    @NotNull private final ApplicationAuthorization auth;

    @NotNull private final String challenge;

    private final long createTime;

    /** Expire time in seconds. */
    private final long expire;

    private volatile boolean expired = false;

    public Nonce(@NotNull ApplicationAuthorization auth, @NotNull String challenge, long expire) {
        if (expire <= 0) {
            throw new IllegalArgumentException("Expire time can positive.");
        }

        if (auth == null) {
            System.out.println("wired");
        }

        id = sequence.incrementAndGet();
        this.auth = auth;
        this.challenge = challenge;
        this.createTime = System.currentTimeMillis();
        this.expire = expire * 1000L;
    }

    @Override
    public boolean expired() {
        return System.currentTimeMillis() >= createTime + expire;
    }
}
