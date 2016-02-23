package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.support.persist.NasPersistence;
import cn.com.xinli.portal.util.AddressUtil;
import cn.com.xinli.portal.util.Serializer;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * NAS store based on <a href="http://ehcache.org">EhCache</a>.
 *
 * <p>{@link #nasCache} saves full-populated-json-formatted NAS/BRAS
 * devices information with key: nas-name.
 *
 * <p>{@link #nasMappingCache} saves ip:mac -> nas ip.
 *
 * <p>Add a NAS/BRAS device. By calling on {@link Nas#getTranslation()}
 * and {@link CredentialsTranslation#getModifiers()}
 * methods, force lazy-initialization to load details of {@link Nas}es.
 * in</tt>, by <tt>ip address range</tt>.
 *
 * <p>Loading {@link NasRule}s and {@link Nas} devices should be handled carefully.
 * Because the central entity, {@link Nas}'s details are lazy-initial fetched.
 * see {@link #reload()}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
@Profile("standalone")
public class EhcacheNasStore implements NasStore {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(EhcacheNasStore.class);

    @Autowired
    private Ehcache nasCache;

    @Autowired
    private Ehcache nasRuleCache;

    @Autowired
    private Ehcache nasSearchCache;

    @Autowired
    private Ehcache nasMappingCache;

    @Autowired
    private Serializer<Nas> nasSerializer;

    @Autowired
    private Serializer<NasRule> nasRuleSerializer;

    @Autowired
    private NasPersistence nasPersistence;

    /**
     * Get nas from cache element.
     * @param element cache element.
     * @return nas.
     */
    private Nas toNas(Element element) {
        return nasSerializer.deserialize((byte[]) element.getObjectValue());
    }

    private NasRule toNasRule(Element element) {
        return nasRuleSerializer.deserialize((byte[]) element.getObjectValue());
    }

    /**
     * Transfer nas into a cache element.
     * @param nas nas.
     * @return cache element.
     */
    private Element toElement(String key, Nas nas) {
        final byte[] value = nasSerializer.serialize(nas);
        return new Element(key, value);
    }

    private Element toElement(NasRule rule) {
        final byte[] value = nasRuleSerializer.serialize(rule);
        return new Element(rule.getId(), value);
    }

    @Override
    public void reload() {
        logger.info("ehcache nas store reloading...");
        nasCache.removeAll();
        nasRuleCache.removeAll();
        nasSearchCache.removeAll();
        load();
        logger.info("ehcache nas store reloaded.");
    }

    /**
     * Load all NAS/BRAS devices and rules for those devices.
     *
     * <p>Since {@link NasRule}s fetch associated {@link Nas} with
     * {@link FetchType#EAGER} mode and {@link Nas}'s
     * {@link CredentialsTranslation} will be fetched by {@link FetchType#LAZY}
     * mode, we need force {@link #nasPersistence} to load NAS details with
     * lazy-loading.
     *
     * <p>Results of calling this method are:
     * <br>{@link NasRule}s are loaded with <em>partial</em> {@link Nas} into
     * {@link #nasPersistence}, {@link Nas#translation} is not loaded yet, but its sufficient for server.
     * <br>{@link Nas}s are populated with <em>full-loaded</em> {@link Nas}es and
     * saved in {@link #nasPersistence}.
     */
    private void load() {
        nasPersistence.rules().forEach(this::addRule);
        nasPersistence.devices().forEach(this::addDevice);

        logger.info("{} devices loaded.", nasCache.getSize());
        logger.info("{} rules loaded.", nasRuleCache.getSize());
    }

    /**
     * Add nas rule to store without persisting.
     * @param rule nas rule.
     */
    private void addRule(NasRule rule) {
        nasRuleCache.put(toElement(rule));
    }

    /**
     * Add devices to store without persisting.
     *
     * <p>By calling on {@link Nas#getTranslation()} and {@link CredentialsTranslation#getModifiers()}
     * methods, force lazy-initialization to load details of {@link Nas}es.
     * @param nas NAS/BRAS device.
     */
    private void addDevice(Nas nas) {
        nasCache.put(toElement(nas.getName(), nas));
        nasSearchCache.put(toElement(nas.getIp(), nas));
    }

    @Override
    public Nas get(String name) throws NasNotFoundException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        Element element = nasCache.get(name);
        if (element == null) {
            throw new NasNotFoundException(name);
        }

        return toNas(element);
    }

    /**
     * {@inheritDoc}
     *
     * Put nas into cache and save to database.
     * @param nas NAS/BRAS device.
     */
    @Override
    public void put(Nas nas) {
        Objects.requireNonNull(nas, Nas.EMPTY_NAS);
        addDevice(nas);
        nasPersistence.save(nas);
    }

    @Override
    public boolean exists(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        return nasCache.get(name) != null;
    }

    @Override
    public boolean delete(String name) throws NasNotFoundException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        if (!exists(name)) {
            throw new NasNotFoundException(name);
        }
        nasPersistence.delete(name);
        return nasCache.remove(name);
    }

    @Override
    public Nas find(String ip) throws NasNotFoundException {
        if (StringUtils.isEmpty(ip)) {
            throw new IllegalArgumentException("nas ip can not be blank.");
        }

        Element element = nasSearchCache.get(ip);
        if (element == null) {
            throw new NasNotFoundException(ip);
        }

        return toNas(element);
    }

    /**
     * {@inheritDoc}
     *
     * Put nas rule into cache and save to database.
     * @param rule NAS/BRAS device rule.
     */
    @Override
    public NasRule put(NasRule rule) {
        Objects.requireNonNull(rule, NasRule.EMPTY_RULE);
        addRule(rule);
        return nasPersistence.save(rule);
    }

    @Override
    public Stream<NasRule> rules() {
        List<NasRule> rules = new ArrayList<>();
        for (Object key : nasRuleCache.getKeys()) {
            Element element = nasRuleCache.get(key);
            if (element != null) {
                rules.add(toNasRule(element));
            }
        }
        return rules.stream();
    }

    @Override
    public Stream<Nas> devices() {
        List<Nas> devices = new ArrayList<>();
        for (Object key : nasCache.getKeys()) {
            Element element = nasCache.get(key);
            if (element != null) {
                devices.add(toNas(element));
            }
        }
        return devices.stream();
    }

    @Override
    public Nas locate(Credentials credentials) throws NasNotFoundException {
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);
        final String key = Session.pair(credentials.getIp(), credentials.getMac());
        Element element = nasMappingCache.get(key);
        if (element != null) {
            Nas nas = find((String) element.getObjectValue());
            if (nas != null) {
                return nas;
            }
        }

        throw new NasNotFoundException("user:{" + key + "}");
    }

    @Override
    public void map(String ip, String mac, String nasIp) throws NasNotFoundException {
        if (StringUtils.isEmpty(ip) ||
                StringUtils.isEmpty(mac) ||
                StringUtils.isEmpty(nasIp)) {
            throw new IllegalArgumentException("ip, mac and nasIp can not be blank.");
        }

        final String formatted = AddressUtil.formatMac(mac);
        Nas nas = find(nasIp);
        if (nas == null) {
            throw new NasNotFoundException(nasIp);
        }

        final String key = Session.pair(ip, formatted);
        Element element = new Element(key, nasIp);
        nasMappingCache.put(element);
    }
}
