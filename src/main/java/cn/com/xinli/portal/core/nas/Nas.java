package cn.com.xinli.portal.core.nas;

import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import com.fasterxml.jackson.annotation.*;
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
 * <p>To make jackson-json be able to deserialize to an abstract class (like this class),
 * this class was annotated with {@link JsonTypeInfo} to add additional subclass
 * information in JSON, and with {@link JsonSubTypes} to support mapper serializer.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(schema = "PWS", name = "nas")
@JsonInclude
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "nas_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HuaweiNas.class, name = "HUAWEI"),
        @JsonSubTypes.Type(value = CmccNas.class, name = "CMCC"),
        @JsonSubTypes.Type(value = RadiusNas.class, name = "RADIUS"),
})
public abstract class Nas {

    public static final String EMPTY_NAS = "NAS is empty.";

    /** Auto generated internal id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    /** Nas name. */
    @Column(unique = true, nullable = false)
    @JsonProperty
    private String name;

    /** IPv4 Address. */
    @Column(name = "ipv4_address")
    @JsonProperty("ipv4_address")
    private String ipv4Address;

    /** IPv6 Address. */
    @Column(name = "ipv6_address")
    @JsonProperty("ipv6_address")
    private String ipv6Address;

    /** Associated translation. */
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "translation_id", referencedColumnName = "id")
    @JsonProperty
    private CredentialsTranslation translation;

    /**
     * Get nas type string.
     * @return nas type string.
     */
    @JsonIgnore
    public abstract NasType getType();

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
    @JsonIgnore
    public String getIp() {
        return StringUtils.isEmpty(getIpv4Address()) ? getIpv6Address() : getIpv4Address();
    }

    /**
     * Get nas' {@link InetAddress}.
     * @return nas' {@link InetAddress}.
     * @throws UnknownHostException
     */
    @JsonIgnore
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
                ", type=" + getType().name() +
                ", translation=" + translation +
                '}';
    }
}