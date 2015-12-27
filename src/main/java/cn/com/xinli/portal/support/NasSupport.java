package cn.com.xinli.portal.support;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.NasType;
import cn.com.xinli.portal.PortalException;
import cn.com.xinli.portal.AuthType;
import cn.com.xinli.portal.util.AddressUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * Device (NAS/BRAS) supports Portal protocol configuration.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class NasSupport implements Nas {
    /**
     * Default nas type name.
     */
    NasType DEFAULT_NAS_TYPE = NasType.HuaweiV2;

    /**
     * Default nas listen port.
     */
    int DEFAULT_NAS_LISTEN_PORT = 2000;

    /**
     * Default NAS authentication type.
     */
    AuthType DEFAULT_NAS_AUTHENTICATION_TYPE = AuthType.CHAP;

    /**
     * NAS id.
     */
    private final long id;

    /**
     * NAS id.
     * @see {@link Nas#getNasId()}
     */
    private final String nasId;

    /**
     * IPv4 Address.
     */
    private final String ipv4Address;

    /**
     * IPv6 Address.
     */
    private final String ipv6Address;

    /**
     * Nas type.
     */
    private final NasType type;

    /**
     * Portal listen port.
     */
    private final int listenPort;

    /**
     * Authentication type (PAP/CHAP).
     */
    private final AuthType authType;

    /**
     * Shared secret.
     */
    private final String sharedSecret;

    /**
     * Ipv4 start address in {@link Integer} form.
     */
    private final int ipv4start;

    /**
     * Ipv4 end address in {@link Integer} form.
     */
    private final int ipv4end;

    /**
     * Sole constructor.
     */
    private NasSupport(@NotNull long id,
                       String nasId,
                       String ipv4Address,
                       String ipv6Address,
                       String type,
                       int listenPort,
                       String authType,
                       String sharedSecret,
                       int ipv4start,
                       int ipv4end) {
        this.id = id;
        this.nasId = nasId;
        this.sharedSecret = sharedSecret;
        this.ipv4Address = StringUtils.isEmpty(ipv4Address) ? "" : ipv4Address;
        this.ipv6Address = StringUtils.isEmpty(ipv6Address) ? "" : ipv6Address;
        this.type = StringUtils.isEmpty(type) ? DEFAULT_NAS_TYPE : NasType.valueOf(type);
        this.listenPort = listenPort <= 0 ? DEFAULT_NAS_LISTEN_PORT : listenPort;
        this.authType = StringUtils.isEmpty(authType) ? DEFAULT_NAS_AUTHENTICATION_TYPE : AuthType.of(authType);
        this.ipv4start = ipv4start;
        this.ipv4end = ipv4end;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getNasId() {
        return nasId;
    }

    @Override
    public String getIpv4Address() {
        return ipv4Address;
    }

    @Override
    public String getIpv6Address() {
        return ipv6Address;
    }

    @Override
    public NasType getType() {
        return type;
    }

    @Override
    public int getListenPort() {
        return listenPort;
    }

    @Override
    public AuthType getAuthType() {
        return authType;
    }

    @Override
    public int getIpv4end() {
        return ipv4end;
    }

    @Override
    public int getIpv4start() {
        return ipv4start;
    }

    @Override
    public String getSharedSecret() {
        return sharedSecret;
    }

    @Override
    public String toString() {
        return "NasSupport{" +
                "authType='" + authType + '\'' +
                ", id=" + id +
                ", nasId='" + nasId + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", type='" + type + '\'' +
                ", listenPort=" + listenPort +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", ipv4start=" + ipv4start +
                ", ipv4end=" + ipv4end +
                '}';
    }

    /**
     * Create an unmodifiable NAS from configuration.
     *
     * @param configuration nas configuration.
     * @return NAS.
     * @throws PortalException
     */
    public static Nas build(NasConfiguration configuration) throws PortalException {
        if (StringUtils.isEmpty(configuration.getIpv4Address())
                && StringUtils.isEmpty(configuration.getIpv6Address())) {
            throw new PortalException("NAS must has ipv4 or ipv6 address at lest.") {
            };
        }
        String ipv4start = configuration.getIpv4start(),
                ipv4end = configuration.getIpv4end();

        int start, end;
        try {
            start = AddressUtil.convertIpv4Address(ipv4start);
            end = AddressUtil.convertIpv4Address(ipv4end);
        } catch (IllegalArgumentException iae) {
            start = 0;
            end = 0;
        }

        return new NasSupport(configuration.getId(),
                configuration.getNasId(),
                configuration.getIpv4Address(),
                configuration.getIpv6Address(),
                configuration.getType(),
                configuration.getListenPort(),
                configuration.getAuthType().toUpperCase(),
                configuration.getSharedSecret(),
                start,
                end);
    }

}
