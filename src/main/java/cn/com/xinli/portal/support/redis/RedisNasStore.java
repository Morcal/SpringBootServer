package cn.com.xinli.portal.support.redis;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.support.configuration.ClusterConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * NAS/BRAS device store.
 *
 * <p>Each NAS/BRAS device will be saved 2 different places in REDIS.
 * <ul>
 *     <li>nas:name -> serialized nas</li> as primary.
 *     <li>nas:ip -> nas name</li> to support searching.
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Component
@Profile("cluster")
public class RedisNasStore implements NasStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RedisNasStore.class);

    public static final String ID = "id:nas";

    public static final String RULE_LIST_KEY = "nas:rule";

    @Autowired
    @Qualifier("redisNasTemplate")
    private RedisTemplate<String, Nas> redisNasTemplate;

    @Autowired
    @Qualifier("redisNasRuleTemplate")
    private RedisTemplate<String, NasRule> redisNasRuleTemplate;

    @Autowired
    @Qualifier("redisQueryTemplate")
    private StringRedisTemplate redisQueryTemplate;

    @Autowired
    @Qualifier("redisIdTemplate")
    private RedisTemplate<String, Long> redisIdTemplate;

    /**
     * Generate a REDIS value key as "nas:name" and "nas:ip" so
     * that it can be searched.
     * @param value additional info value.
     * @return REDIS key.
     */
    String keyFor(String value) {
        return "nas:" + value;
    }

    String keyFor(Nas nas) {
        return keyFor(nas.getName());
    }

    String keyFor(String ip, String mac) {
        return "nas:" + ip + ":" + mac;
    }
    String keyFor(Pair<String, String> pair) {
        return keyFor(pair.getKey(), pair.getValue());
    }

    void ensureId(Nas nas) {
        long id = redisIdTemplate.opsForValue().increment(ID, 1);
        nas.setId(id);
    }

    @Override
    public void reload() {
        /* Since data stored in REDIS, nothing to do. */
        logger.info("nas reload, no-op");
    }

    @Override
    public Nas get(String id) throws NasNotFoundException {
        Nas nas = redisNasTemplate.opsForValue().get(keyFor(id));
        if (nas == null) {
            throw new NasNotFoundException(id);
        }
        return nas;
    }

    @Override
    public Stream<Nas> search(String value) {
        return Stream.empty();
    }

    @Override
    public Nas find(String ip) throws NasNotFoundException {
        String name = redisQueryTemplate.opsForValue().get(keyFor(ip));
        if (name != null) {
            Nas nas = redisNasTemplate.opsForValue().get(keyFor(name));
            if (nas != null) {
                return nas;
            }
        }
        throw new NasNotFoundException(ip);
    }

    @Override
    public NasRule put(NasRule rule) {
        Objects.requireNonNull(rule, NasRule.EMPTY_RULE);
        redisNasRuleTemplate.opsForSet().add(RULE_LIST_KEY, rule);
        return rule;
    }

    @Override
    public Stream<NasRule> rules() {
        //long size = redisNasRuleTemplate.opsForSet().size(RULE_LIST_KEY);
        /* We assume that nas rule list will not be long (for example thousands). */
        return redisNasRuleTemplate.opsForSet().members(RULE_LIST_KEY).stream();
    }

    @Override
    public Stream<Nas> devices() {
        List<Nas> results = new ArrayList<>();
        redisNasTemplate.keys("nas:*").forEach(s -> {
            Nas nas = redisNasTemplate.opsForValue().get(s);
            if (nas != null) {
                results.add(nas);
            }
        });

        return results.stream();
    }

    @Override
    public void put(Nas nas) {
        Objects.requireNonNull(nas, Nas.EMPTY_NAS);
        /* nas:name. */
        redisNasTemplate.opsForValue().set(keyFor(nas), nas);
        /* nas:ip for searching. */
        redisQueryTemplate.opsForValue().set(keyFor(nas.getIp()), nas.getName());

        if (logger.isTraceEnabled()) {
            logger.trace("nas saved in store, {}", nas);
        }

        /* Notify NAS/BRAS added. */
        NasMessage message = new NasMessage();
        message.setType(NasMessage.Type.ADDED);
        message.setNas(nas);
        redisNasTemplate.convertAndSend(ClusterConfiguration.NAS_CHANNEL, message);

        if (logger.isTraceEnabled()) {
            logger.trace("nas add notified, {}", nas);
        }
    }

    @Override
    public boolean exists(String id) {
        return redisNasTemplate.opsForValue().get(keyFor(id)) != null;
    }

    @Override
    public boolean delete(String id) throws NasNotFoundException {
        Nas nas = get(id);
        redisNasTemplate.delete(keyFor(nas));
        redisQueryTemplate.delete(keyFor(nas.getIp()));
        boolean removed = !exists(nas.getName());

        if (logger.isTraceEnabled()) {
            logger.trace("session removed: {}, {}", id, removed);
        }

        if (removed) {
            NasMessage message = new NasMessage();
            message.setType(NasMessage.Type.REMOVED);
            message.setNas(nas);
            redisNasTemplate.convertAndSend(ClusterConfiguration.NAS_CHANNEL, message);

            if (logger.isTraceEnabled()) {
                logger.trace("session remove notified, {}", nas);
            }
        }

        return removed;
    }

    @Override
    public Nas locate(Pair<String, String> pair) throws NasNotFoundException {
        Objects.requireNonNull(pair, "locate nas pair can not be null");
        Nas nas = redisNasTemplate.opsForValue().get(keyFor(pair));
        if (nas == null) {
            throw new NasNotFoundException(pair.toString());
        }
        return nas;
    }

    @Override
    public void map(String ip, String mac, String nasIp) throws NasNotFoundException {
        if (StringUtils.isEmpty(ip) ||
                StringUtils.isEmpty(mac) ||
                StringUtils.isEmpty(nasIp)) {
            throw new IllegalArgumentException("ip, mac and nasIp can not be blank.");
        }

        Nas nas = find(nasIp);
        redisNasTemplate.opsForValue().set(keyFor(ip, mac), nas);
    }
}
