package cn.com.xinli.portal.support.configuration;

import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.core.ratelimiting.AccessTimeTrack;
import cn.com.xinli.portal.core.session.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * Cluster configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Configuration
@Profile("cluster")
public class ClusterConfiguration {

    public static final String NAS_CHANNEL = "nas";

    public static final String CERTIFICATE_CHANNEL = "certificate";

    public static final String SESSION_CHANNEL = "session";

    private static final int DEFAULT_REDIS_SENTINEL_PORT = 26379;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisSentinelConfiguration config = new RedisSentinelConfiguration()
                .master(serverConfiguration.getClusterConfiguration().getRedisMaster());

        for (String sentinel : serverConfiguration.getClusterConfiguration().getRedisSentinels()) {
            String[] sen = sentinel.split(":");
            config.sentinel(sen[0],
                    sen.length > 1 ? Integer.valueOf(sen[1]) : DEFAULT_REDIS_SENTINEL_PORT);
        }
        return new JedisConnectionFactory(config);
    }

    @Bean(name = "redisSessionTemplate")
    public RedisTemplate<String, Session> redisSessionTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Session> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Session> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Session.class);
        ObjectMapper om = new ObjectMapper();
        //om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisCertificateTemplate")
    public RedisTemplate<String, Certificate> redisCertificateTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Certificate> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Certificate> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Certificate.class);
        ObjectMapper om = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisNasTemplate")
    public RedisTemplate<String, Nas> redisNasTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Nas> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<Nas> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Nas.class);
        ObjectMapper om = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisNasRuleTemplate")
    public RedisTemplate<String, NasRule> redisNasRuleTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, NasRule> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<NasRule> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(NasRule.class);
        ObjectMapper om = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisTrackTemplate")
    public RedisTemplate<String, AccessTimeTrack> redisTrackTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, AccessTimeTrack> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer<AccessTimeTrack> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(AccessTimeTrack.class);
        ObjectMapper om = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisQueryTemplate")
    public StringRedisTemplate redisQueryTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisIdTemplate")
    public RedisTemplate<String, Long> redisIdTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
