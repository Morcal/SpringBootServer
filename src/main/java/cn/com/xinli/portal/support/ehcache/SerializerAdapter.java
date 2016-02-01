package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.util.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Session Serializer Adapter.
 *
 * <p>This class serialize/deserialize session into/from bytes by employing
 * {@link Jackson2JsonRedisSerializer}. The serialization process should
 * be fully populated, so any associated members must be loaded if they
 * are marked as LAZY. This class uses {@link Hibernate4Module} to enable
 * {@link Hibernate4Module.Feature#FORCE_LAZY_LOADING}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class SerializerAdapter<T> implements Serializer<T> {
    /** Underlying JSON serializer. */
    private final Jackson2JsonRedisSerializer<T> delegate;

    public SerializerAdapter(Class<T> cls) {
        delegate = new Jackson2JsonRedisSerializer<>(cls);
        ObjectMapper mapper = new ObjectMapper();
        Hibernate4Module module = new Hibernate4Module();
        module.enable(Hibernate4Module.Feature.FORCE_LAZY_LOADING);
        mapper.registerModule(module);
        delegate.setObjectMapper(mapper);
    }

    @Override
    public byte[] serialize(T object) {
        return delegate.serialize(object);
    }

    @Override
    public T deserialize(byte[] bytes) {
        return delegate.deserialize(bytes);
    }
}
