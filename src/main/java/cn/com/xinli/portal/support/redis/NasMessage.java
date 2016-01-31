package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.nas.Nas;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * NAS/BRAS device message.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@JsonInclude
public class NasMessage {
    enum Type {
        ADDED,
        REMOVED,
        SUSPENDED
    }

    @JsonProperty
    private Type type;

    @JsonProperty
    private Nas nas;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Nas getNas() {
        return nas;
    }

    public void setNas(Nas nas) {
        this.nas = nas;
    }
}
