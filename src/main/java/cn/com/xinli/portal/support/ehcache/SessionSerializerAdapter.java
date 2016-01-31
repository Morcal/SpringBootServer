package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.util.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Session Serializer Adapter.
 *
 * <p>This class serialize/deserialize session into/from bytes by employing
 * {@link Jackson2JsonRedisSerializer}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class SessionSerializerAdapter implements Serializer<Session> {
    /** Underlying JSON serializer. */
    private final Jackson2JsonRedisSerializer<Session> delegate;

    public SessionSerializerAdapter() {
        delegate = new Jackson2JsonRedisSerializer<>(Session.class);
        ObjectMapper mapper = new ObjectMapper();
        delegate.setObjectMapper(mapper);
    }

    @Override
    public byte[] serialize(Session session) {
        return delegate.serialize(session);
    }

    @Override
    public Session deserialize(byte[] bytes) {
        return delegate.deserialize(bytes);
    }
}
