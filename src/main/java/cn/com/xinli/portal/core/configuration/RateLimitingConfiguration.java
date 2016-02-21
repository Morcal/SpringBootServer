package cn.com.xinli.portal.core.configuration;

/**
 * Rate-Limiting Configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class RateLimitingConfiguration {
    /** Allow maximum rate. */
    private int rate;

    /** Time to live. */
    private int ttl;

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "RateLimitingConfiguration{" +
                "rate=" + rate +
                ", ttl=" + ttl +
                '}';
    }
}
