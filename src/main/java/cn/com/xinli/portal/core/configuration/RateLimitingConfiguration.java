package cn.com.xinli.portal.core.configuration;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class RateLimitingConfiguration {
    private int rate;
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
