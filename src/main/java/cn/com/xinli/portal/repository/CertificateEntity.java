package cn.com.xinli.portal.repository;

import cn.com.xinli.portal.core.Certificate;

import javax.persistence.*;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="certificate")
public class CertificateEntity implements Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "app_id", unique = true)
    private String appId;

    @Column(name = "shared_secret")
    private String sharedSecret;

    @Column
    private String vendor;

    @Column
    private String os;

    @Column
    private String version;

    @Column
    private boolean disabled;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
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

    @Override
    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    @Override
    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "CertificateEntity{" +
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
