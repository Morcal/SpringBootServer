package cn.com.xinli.portal.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Nas Mapping.
 *
 * <p>This class contains NAS/BRAS and clients mappings.</p>
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class NasMapping {
    /** Log. */
    private static final Log log = LogFactory.getLog(NasMapping.class);

    //@Autowired
    private final Map<String, Nas> configured = Collections.synchronizedMap(new HashMap<>());;

    /** NAS/BRAS devices, key: nas id, value: nas configuration. */
    private final Map<String, Nas> devices = Collections.synchronizedMap(new HashMap<>());

    /** User, Nas mapping, key: user info pair, value: nas id. */
    private final Map<String, String> userNasMapping = new ConcurrentHashMap<>();

    public void init() {
        synchronized (devices) {
            log.debug("Initialize configured nas, count: " + configured.size());
            devices.putAll(configured);
        }
    }

    public void setConfigured(Map<String, Nas.Config> configured) {
        configured.values().stream().forEach(cfg -> {
            synchronized (configured) {
                try {
                    this.configured.put(cfg.getId(), Nas.fromConfig(cfg));
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                    log.fatal("系统配置错误，NAS配置错误: " + cfg);
                    System.exit(1);
                }
            }
        });
    }

    /**
     * Create a pair string for ip and mac.
     * @param ip ip address.
     * @param mac mac address.
     * @return paired string.
     */
    private static String pair(String ip, String mac) {
        //FIXME return StringUtils.join(ip, " ", mac);
        return ip + " " + mac;
    }

    /**
     * Create a mapping from user ip and mac to NAS device configuration.
     * @param userIp user ip.
     * @param userMac user mac.
     * @param nasIp NAS ip.
     * @throws ConfigurationException
     */
    public void map(String userIp, String userMac, String nasIp) throws ConfigurationException {
        String pair = pair(userIp, userMac);
        synchronized (devices) {
            Optional<Nas> nas = devices.values().stream()
                    .filter(device -> device.getIpv4Address().equals(nasIp) ||
                            device.getIpv6Address().equals(nasIp)
                    ).findFirst();

            nas.ifPresent(n -> {
                log.debug("mapping nas: " + n + ", pair: " + pair);
                userNasMapping.putIfAbsent(pair, n.getId());
            });

            nas.orElseThrow(() -> new ConfigurationException(
                    "NAS with ip: " + nasIp + " not found."));
        }
    }

    /**
     * Find NAS for given user ip and user mac.
     * @param userIp user ip address.
     * @param userMac user mac address.
     * @return NAS matches ip and mac, or null if not found.
     */
    public Nas findNas(String userIp, String userMac) {
        String pair = pair(userIp, userMac);
        String nasId = userNasMapping.get(pair);

        if (nasId != null) {
            synchronized (devices) {
                return devices.get(nasId);
            }
        }

        return null;
    }
}
