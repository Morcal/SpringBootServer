package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

/**
 * Cluster configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterConfiguration {
    /** REDIS master name. */
    @JsonProperty("redis_master")
    private String redisMaster;

    /** REDIS sentinel ip addresses. */
    @JsonProperty("redis_sentinels")
    private String[] redisSentinels;

    public String getRedisMaster() {
        return redisMaster;
    }

    public void setRedisMaster(String redisMaster) {
        this.redisMaster = redisMaster;
    }

    public String[] getRedisSentinels() {
        return redisSentinels;
    }

    public void setRedisSentinels(String[] redisSentinels) {
        this.redisSentinels = redisSentinels;
    }

    @Override
    public String toString() {
        return "ClusterConfiguration{" +
                "redisMaster='" + redisMaster + '\'' +
                ", redisSentinels=" + Arrays.toString(redisSentinels) +
                '}';
    }
}
