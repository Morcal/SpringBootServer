package cn.com.xinli.portal.support;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.NasRepository;
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
    private Set<NasConfiguration> configured;

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
            logger.info("> Loading configured nas, count: {}.", configured.size());
            configured.forEach(configuration -> devices.put(configuration.getId(), NasSupport.build(configuration)));
            logger.info("> Loading nas from database.");
            nasRepository.all().forEach(nas -> devices.put(nas.getId(), nas));
        }
    }

    /**
     *  Inject pre-configured nas devices.
     *
     * @param configured nas pre-configured.
     */
    public void setConfigured(Set<NasConfiguration> configured) {
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
                    logger.debug("> mapping nas: {}, pair: {}.", n, pair);
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

    @Override
    public Nas findNas(String userIp, String userMac) {
        String pair = Session.pair(userIp, userMac);
        Long nasId = userNasMapping.get(pair);

        if (nasId != null) {
            synchronized (devices) {
                return devices.get(nasId);
            }
        }

        return null;
    }

    @Override
    public Nas findByIpv4Range(int ip) {
        for (Nas nas : devices.values()) {
            if (nas.getIpv4start() <= ip && nas.getIpv4end() >= ip) {
                return nas;
            }
        }
        return null;
    }
}
