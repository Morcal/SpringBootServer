package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Rate-Limiting Configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateLimitingConfiguration {
    /** Allow maximum rate. */
    @JsonProperty
    private int rate;

    /** Time to live. */
    @JsonProperty
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
