package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.NasMapping;
import cn.com.xinli.portal.core.Session;
import cn.com.xinli.portal.repository.NasRepository;
import cn.com.xinli.portal.protocol.Nas;
import cn.com.xinli.portal.protocol.NasNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PWS {@link Nas} mapping.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class NasMappingSupport implements NasMapping {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(NasMappingSupport.class);

    @Autowired
    private NasRepository nasRepository;

    /** Configured NAS devices. in nas.xml */
    private Set<Nas> configured;

    /** NAS/BRAS devices, key: nas id, value: nas configuration. */
    private final Map<Long, Nas> devices = Collections.synchronizedMap(new HashMap<>());

    /** User, Nas mapping, key: user info pair, value: nas id. */
    private final Map<String, Long> userNasMapping = new ConcurrentHashMap<>();

    /**
     * Initialize NAS Mapping.
     *
     * Add all configured NAS devices to running devices.
     */
    public void init() {
        reload();
    }

    @Override
    public void reload() {
        userNasMapping.clear();
        synchronized (devices) {
            logger.info("Loading configured nas, count: {}.", configured.size());
            configured.forEach(nas -> devices.put(nas.getId(), nas));
            logger.info("Loading nas from database.");
            nasRepository.all().forEach(nas -> devices.put(nas.getId(), new NasAdapter(nas)));
        }
    }

    /**
     *  Inject pre-configured nas devices.
     *
     * @param configured nas pre-configured.
     */
    public void setConfigured(Set<Nas> configured) {
        this.configured = configured;
    }

    @Override
    public void map(String userIp, String userMac, String nasIp) throws NasNotFoundException {
        String pair = Session.pair(userIp, userMac);
        synchronized (devices) {
            Optional<Nas> nas = devices.values().stream()
                    .filter(device -> device.getIpv4Address().equals(nasIp) ||
                            device.getIpv6Address().equals(nasIp)
                    ).findFirst();

            nas.ifPresent(n -> {
                if (logger.isDebugEnabled()) {
                    logger.debug("mapping nas: {}, pair: {}.", n, pair);
                }
                userNasMapping.put(pair, n.getId());
            });

            nas.orElseThrow(() -> new NasNotFoundException("NAS with ip: " + nasIp + " not found."));
        }
    }

    @Override
    public Optional<Nas> getNas(long id) {
        synchronized (devices) {
            return Optional.ofNullable(devices.get(id));
        }
    }

    @Override
    public Optional<Nas> getNasByNasId(String nasId) {
        return devices.values().stream()
                .filter(nas -> nas.getNasId().equalsIgnoreCase(nasId))
                .findFirst();
    }

    /**
     * Find NAS for given user ip and user mac.
     *
     * @param userIp  user ip address.
     * @param userMac user mac address.
     * @return NAS matches ip and mac, or null if not found.
     */
    private Optional<Nas> find(String userIp, String userMac) {
        String pair = Session.pair(userIp, userMac);
        Long nasId = userNasMapping.get(pair);

        if (nasId != null) {
            synchronized (devices) {
                return Optional.ofNullable(devices.get(nasId));
            }
        }

        return Optional.empty();
    }

    /**
     * Find nas by ipv4 address range.
     *
     * @param ip ip v4 address.
     * @return nas found if matches or null.
     */
    private Optional<Nas> findByIpv4Range(String ip) {
        synchronized (devices) {
            return devices.values().stream()
                    .filter(n -> n.contains(ip))
                    .findAny();
        }
    }

    /**
     * Find NAS for given domain.
     * @param domain domain.
     * @return NAS supports domain if found.
     */
    private Optional<Nas> find(String domain) {
        synchronized (devices) {
            return devices.values().stream()
                    .filter(d -> d.containsDomain(domain))
                    .findAny();
        }
    }

    /**
     * Find user incoming NAS.
     * @param username user name.
     * @param ip ip address.
     * @param mac mac address.
     * @return NAS if found.
     * @throws NasNotFoundException
     */
    @Override
    public Nas find(String username, String ip, String mac) throws NasNotFoundException {
        Optional<Nas> nas = Optional.empty();
        if (!StringUtils.isEmpty(username) && username.contains("@")) {
            String domain = username.substring(username.indexOf("@"));
            nas = find(domain);
        }

        if (!nas.isPresent()) {
            nas = find(ip, mac);
            if (!nas.isPresent()) {
                if (logger.isTraceEnabled()) {
                    logger.trace("incoming request not mapped (through web redirect), trying ip range.");
                }
                /* Last resort, try to find nas by ipv4 range. */
                nas = findByIpv4Range(ip);
                if (!nas.isPresent()) {
                    throw new NasNotFoundException(ip, mac);
                }
            }
        }

        return nas.get();
    }

}
