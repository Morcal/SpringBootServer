package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.session.Session;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Session message.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@JsonInclude
public class SessionMessage {
    enum Type {
        ADDED,
        REMOVED,
        SUSPENDED
    }

    @JsonProperty
    private Type type;

    @JsonProperty
    private Session session;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
