package cn.com.xinli.portal.support.ehcache;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.nas.NasRule;
import cn.com.xinli.portal.core.nas.NasStore;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.util.Serializer;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
@Profile("standalone")
public class EhcacheNasStore implements NasStore {
    @Autowired
    private Ehcache nasCache;

    @Autowired
    private Ehcache nasMappingCache;

    @Autowired
    private Serializer<Nas> nasSerializer;

    /**
     * Get nas from cache element.
     * @param element cache element.
     * @return nas.
     */
    private Nas fromElement(Element element) {
        return nasSerializer.deserialize((byte[]) element.getObjectValue());
    }

    /**
     * Transfer nas into a cache element.
     * @param nas nas.
     * @return cache element.
     */
    private Element toElement(Nas nas) {
        final String key = nas.getName();
        final byte[] value = nasSerializer.serialize(nas);
        return new Element(key, value);
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

        return fromElement(element);
    }

    @Override
    public void put(Nas nas) {
        Objects.requireNonNull(nas);
        nasCache.put(toElement(nas));
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
        return nasCache.remove(name);
    }

    @Override
    public Nas find(String ip) throws NasNotFoundException {
        return null;
    }

    @Override
    public NasRule put(NasRule rule) {
        return null;
    }

    @Override
    public Stream<NasRule> rules() {
        return null;
    }

    @Override
    public Stream<Nas> devices() {
        return null;
    }

    @Override
    public Nas locate(Credentials credentials) throws NasNotFoundException {
        Objects.requireNonNull(credentials);
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

        Nas nas = find(nasIp);
        if (nas == null) {
            throw new NasNotFoundException(nasIp);
        }

        final String key = Session.pair(ip, mac);
        Element element = new Element(key, nasIp);
        nasMappingCache.put(element);
    }
}
