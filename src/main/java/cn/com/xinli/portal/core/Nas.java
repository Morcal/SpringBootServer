package cn.com.xinli.portal.core;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Device (NAS/BRAS) configuration.
 *
 * <p>This interface defines an abstract configuration for NAS/BRAS devices.
 * Portal clients need specific NAS configuration before it can create concrete
 * portal requests.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name = "nas")
public class Nas {
    /** Default nas type name. */
    public static final NasType DEFAULT_NAS_TYPE = NasType.HuaweiV2;

    /** Default nas listen port. */
    public static final int DEFAULT_NAS_LISTEN_PORT = 2000;

    /** Default NAS authentication type. */
    public static final AuthType DEFAULT_NAS_AUTHENTICATION_TYPE = AuthType.CHAP;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** Nas name. */
    @Column(unique = true, nullable = false)
    private String name;

    /** IPv4 Address. */
    @Column(name = "ipv4_address")
    private String ipv4Address;

    /** IPv6 Address. */
    @Column(name = "ipv6_address")
    private String ipv6Address;

    /** Nas type. */
    @Column(nullable = false)
    private NasType type;

    /** Portal listen port. */
    @Column(name = "listen_port", nullable = false)
    private int listenPort;

    /** Authentication type (PAP/CHAP). */
    @Column(name = "authentication_type", nullable = false)
    private AuthType authType;

    @Column(name = "shared_secret", nullable = false)
    private String sharedSecret;

    @ManyToOne
    @JoinColumn(name = "translation_id", referencedColumnName = "id")
    private CredentialsTranslation translation;

    /**
     * Get id.
     * <p>This id is unique and working in scope of PWS only.</p>
     * @return id.
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get nas name.
     * <p>Nas id may not be unique and may be same value
     * which exists in nas' configurations.
     * @return nas id.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get authentication type.
     * @return authentication type.
     */
    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    /**
     * Get ipv4 address.
     * @return ipv4 address.
     */
    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    /**
     * Get ipv6 address.
     * @return ipv6 address.
     */
    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    /**
     * Get nas portal listen port.
     * @return portal listen port.
     */
    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * Get nas type string.
     * @return nas type string.
     */
    public NasType getType() {
        return type;
    }

    public void setType(NasType type) {
        this.type = type;
    }

    /**
     * Get configured shared secret.
     * @return configured shared secret, may be empty but never null.
     */
    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public CredentialsTranslation getTranslation() {
        return translation;
    }

    public void setTranslation(CredentialsTranslation translation) {
        this.translation = translation;
    }


    /**
     * Get nas' ip address.
     * <p>By default, PWS will check nas' ipv4 address first,
     * if not presented, then check ipv6 address.
     * @return ipv4 address if present, or ipv6 address, could be null.
     */
    public String getIp() {
        return StringUtils.isEmpty(getIpv4Address()) ? getIpv6Address() : getIpv4Address();
    }

    /**
     * Get nas' {@link InetAddress}.
     * @return nas' {@link InetAddress}.
     * @throws UnknownHostException
     */
    public InetAddress getNetworkAddress() throws UnknownHostException {
        Objects.requireNonNull(getIp(), "NAS ip can not be empty.");
        return InetAddress.getByName(getIp());
    }

    @Override
    public String toString() {
        return "Nas{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", type=" + type +
                ", listenPort=" + listenPort +
                ", authType=" + authType +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", translation=" + translation +
                '}';
    }
}