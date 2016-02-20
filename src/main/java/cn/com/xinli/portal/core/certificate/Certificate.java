package cn.com.xinli.portal.core.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Authorized client application certificate.
 *
 * <p>Certificates are generated by PWS and shared to the client developer.
 * Portal REST APIs callers need to provide certificate within requests' credentials,
 * so that PWS can identify incoming requests if they are acceptable.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="certificate")
@JsonInclude
public class Certificate {
    public static final String EMPTY_CERTIFICATE = "Certificate is empty.";

    /** Internal id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    /** Client application id. */
    @Column(name = "app_id")
    @JsonProperty
    private String appId;

    /** Shared secret. */
    @Column(name = "shared_secret")
    @JsonProperty
    private String sharedSecret;

    @Column
    @JsonProperty
    private String vendor;

    @Column
    @JsonProperty
    private String os;

    @Column
    @JsonProperty
    private String version;

    @Column
    @JsonProperty
    private boolean disabled;

    /**
     * Get certificate id.
     * @return certificate id.
     */
    public long getId() {
        return id;
    }

    /**
     * Get authorized client/app id.
     * @return client/app id.
     */
    public String getAppId() {
        return appId;
    }

    /**
     * Get shared secret.
     * @return shared secret.
     */
    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    /**
     * Get client/app os.
     * @return client/app os.
     */
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    /**
     * Get authorized vendor name.
     * @return authorized vendor name.
     */
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * Get authorized client/app version.
     * @return authorized client/app version.
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Check if authorization disabled.
     * @return true if disabled.
     */
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "Certificate{" +
                "appId='" + appId + '\'' +
                ", id=" + id +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", vendor='" + vendor + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", disabled=" + disabled +
                '}';
    }
}
