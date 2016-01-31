package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.util.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Session Serializer Adapter.
 *
 * <p>This class serialize/deserialize session into/from bytes by employing
 * {@link Jackson2JsonRedisSerializer}. The serialization process should
 * be fully populated, so any associated members must be loaded if they
 * are marked as LAZY.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class NasSerializerAdapter implements Serializer<Nas> {
    /** Underlying JSON serializer. */
    private final Jackson2JsonRedisSerializer<Nas> delegate;

    public NasSerializerAdapter() {
        delegate = new Jackson2JsonRedisSerializer<>(Nas.class);
        ObjectMapper mapper = new ObjectMapper();
        delegate.setObjectMapper(mapper);
    }

    @Override
    public byte[] serialize(Nas nas) {
        return delegate.serialize(nas);
    }

    @Override
    public Nas deserialize(byte[] bytes) {
        return delegate.deserialize(bytes);
    }
}
