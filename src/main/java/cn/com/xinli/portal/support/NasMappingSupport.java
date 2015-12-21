package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.NasMapping;
import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.persist.NasRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class NasMappingSupport implements NasMapping {
    /** Log. */
    private static final Log log = LogFactory.getLog(NasMappingSupport.class);

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
            log.info("> Loading configured nas, count: " + configured.size());
            configured.forEach(configuration -> devices.put(configuration.getId(), NasSupport.build(configuration)));
            log.info("> Loading nas from database.");
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
    public void map(String userIp, String userMac, String nasIp) throws PortalException {
        String pair = Session.pair(userIp, userMac);
        synchronized (devices) {
            Optional<Nas> nas = devices.values().stream()
                    .filter(device -> device.getIpv4Address().equals(nasIp) ||
                            device.getIpv6Address().equals(nasIp)
                    ).findFirst();

            nas.ifPresent(n -> {
                if (log.isDebugEnabled()) {
                    log.debug("> mapping nas: " + n + ", pair: " + pair);
                }
                userNasMapping.put(pair, n.getId());
            });

            nas.orElseThrow(() -> new PortalException(
                    "NAS with ip: " + nasIp + " not found."){});
        }
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
