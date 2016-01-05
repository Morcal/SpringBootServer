package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.NasType;

import javax.persistence.*;

/**
 * NAS entity.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="nas")
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
    private int ipv4start;

    /** IPv4 range end. */
    @Column(name = "ipv4_end")
    private int ipv4end;

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

    public int getIpv4end() {
        return ipv4end;
    }

    public void setIpv4end(int ipv4end) {
        this.ipv4end = ipv4end;
    }

    public int getIpv4start() {
        return ipv4start;
    }

    public void setIpv4start(int ipv4start) {
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

    @Override
    public String toString() {
        return "NasEntity{" +
                "authType=" + authType +
                ", id=" + id +
                ", nasId='" + nasId + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", type=" + type +
                ", listenPort=" + listenPort +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", ipv4start=" + ipv4start +
                ", ipv4end=" + ipv4end +
                '}';
    }
}
