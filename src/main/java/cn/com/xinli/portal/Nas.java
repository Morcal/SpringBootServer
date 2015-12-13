package cn.com.xinli.portal;

import cn.com.xinli.portal.configuration.ConfigurationException;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Device (NAS/BRAS) support Portal protocol configuration.
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class Nas {
    public static final String DEFAULT_NAS_TYPE = "Huawei";

    public static final int DEFAULT_NAS_LISTENPORT = 2000;

    public static final String DEFAULT_NAS_AUTHTYPE = "CHAP";

    /** NAS id. */
    private String id;

    /** IPv4 Address. */
    private final String ipv4Address;

    /** IPv6 Address. */
    private final String ipv6Address;

    /** Nas type. */
    private final String type;

    /** Portal listen port. */
    private final int listenPort;

    /** Authentication type (PAP/CHAP). */
    private final String authType;

    public Nas(@NotNull String id,
               String ipv4Address,
               String ipv6Address,
               String type,
               int listenPort,
               String authType) {
        this.id = id;
        this.ipv4Address = StringUtils.isEmpty(ipv4Address) ? "" : ipv4Address;
        this.ipv6Address = StringUtils.isEmpty(ipv6Address) ? "" : ipv6Address;
        this.type = StringUtils.isEmpty(type) ? DEFAULT_NAS_TYPE : type;
        this.listenPort = listenPort <= 0 ? DEFAULT_NAS_LISTENPORT : listenPort;
        this.authType = StringUtils.isEmpty(authType) ? DEFAULT_NAS_AUTHTYPE : authType;
    }

    public String getId() {
        return id;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }

    public String getType() {
        return type;
    }

    public int getListenPort() {
        return listenPort;
    }

    public String getAuthType() {
        return authType;
    }

    /**
     * Create an unmodifiable NAS from configuration.
     * @param config nas config.
     * @return NAS.
     * @throws ConfigurationException
     */
    public static Nas fromConfig(Config config) throws ConfigurationException {
        if (StringUtils.isEmpty(config.getIpv4Address())
                && StringUtils.isEmpty(config.getIpv6Address())) {
            throw new ConfigurationException("NAS must has ipv4 or ipv6 address at lest.");
        }
        return new Nas(config.getId(),
                config.getIpv4Address(),
                config.getIpv6Address(),
                config.getType(),
                config.getListenPort(),
                config.getAuthType().toUpperCase());
    }

    public static class Config {
        private String id;
        private String name;
        private String ipv4Address;
        private String ipv6Address;
        private String type;
        private int listenPort;
        private String authType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIpv4Address() {
            return ipv4Address;
        }

        public void setIpv4Address(String ipv4Address) {
            this.ipv4Address = ipv4Address;
        }

        public String getIpv6Address() {
            return ipv6Address;
        }

        public void setIpv6Address(String ipv6Address) {
            this.ipv6Address = ipv6Address;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getListenPort() {
            return listenPort;
        }

        public void setListenPort(int listenPort) {
            this.listenPort = listenPort;
        }

        public String getAuthType() {
            return authType;
        }

        public void setAuthType(String authType) {
            this.authType = authType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Config{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", ipv4Address='" + ipv4Address + '\'' +
                    ", ipv6Address='" + ipv6Address + '\'' +
                    ", type='" + type + '\'' +
                    ", listenPort=" + listenPort +
                    ", authType='" + authType + '\'' +
                    '}';
        }
    }
}
