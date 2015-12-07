package cn.com.xinli.portal.configuration;

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
    private final String id;

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
               String authType) throws ConfigurationException {
        if (StringUtils.isEmpty(ipv4Address) && StringUtils.isEmpty(ipv6Address)) {
            throw new ConfigurationException("NAS must has ipv4 or ipv6 address at lest.");
        }
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
}
