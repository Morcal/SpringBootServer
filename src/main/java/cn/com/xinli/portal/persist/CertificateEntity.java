package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Certificate;

import javax.persistence.*;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="certificate")
@NamedQueries(value = {
        @NamedQuery(name = "CertificateEntity.find",
                query = "select c from CertificateEntity c where c.appId = :appId")
})
public class CertificateEntity implements Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "shared_secret")
    private String sharedSecret;

    @Column
    private String vendor;

    @Column
    private String os;

    @Column
    private String version;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getAppId() {
        return null;
    }

    @Override
    public String getSharedSecret() {
        return null;
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
}
