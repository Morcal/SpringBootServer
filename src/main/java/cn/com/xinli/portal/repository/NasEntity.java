package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.NasType;

import javax.persistence.*;

/**
 * NAS entity.
 *
 * <p>This class save {@link cn.com.xinli.portal.protocol.Nas} in a JPA entity.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name = "nas")
public class NasEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "nas_id", unique = true, nullable = false)
    private String nasId;

    /** Nas name. */
    @Column
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

    /** IPv4 range start. */
    @Column(name = "ipv4_start")
    private String ipv4start;

    /** IPv4 range end. */
    @Column(name = "ipv4_end")
    private String ipv4end;

    /** Supported domains, separated by comma. */
    @Column(name = "supported_domains")
    private String supportedDomains;

    /** Authenticate with domain. */
    @Column
    private boolean authenticateWithDomain;

    @ManyToOne
    @JoinColumn(name = "trans_id", referencedColumnName = "id")
    private CredentialsTranslationEntity translation;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNasId() {
        return nasId;
    }

    public void setNasId(String nasId) {
        this.nasId = nasId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    public String getIpv4end() {
        return ipv4end;
    }

    public void setIpv4end(String ipv4end) {
        this.ipv4end = ipv4end;
    }

    public String getIpv4start() {
        return ipv4start;
    }

    public void setIpv4start(String ipv4start) {
        this.ipv4start = ipv4start;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public NasType getType() {
        return type;
    }

    public void setType(NasType type) {
        this.type = type;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public CredentialsTranslationEntity getTranslation() {
        return translation;
    }

    public void setTranslation(CredentialsTranslationEntity translation) {
        this.translation = translation;
    }

    public String getSupportedDomains() {
        return supportedDomains;
    }

    public void setSupportedDomains(String supportedDomains) {
        this.supportedDomains = supportedDomains;
    }

    public boolean isAuthenticateWithDomain() {
        return authenticateWithDomain;
    }

    public void setAuthenticateWithDomain(boolean authenticateWithDomain) {
        this.authenticateWithDomain = authenticateWithDomain;
    }

    public boolean authenticateWithDomain() {
        return isAuthenticateWithDomain();
    }

    @Override
    public String toString() {
        return "NasEntity{" +
                "id=" + id +
                ", nasId='" + nasId + '\'' +
                ", name='" + name + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", type=" + type +
                ", listenPort=" + listenPort +
                ", authType=" + authType +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", ipv4start='" + ipv4start + '\'' +
                ", ipv4end='" + ipv4end + '\'' +
                ", supportedDomains='" + supportedDomains + '\'' +
                ", authenticateWithDomain=" + authenticateWithDomain +
                ", translation=" + translation +
                '}';
    }
}
