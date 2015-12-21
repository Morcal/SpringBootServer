package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Session;

import javax.persistence.*;
import java.util.Date;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/9.
 */
@Entity
@PersistenceUnit(unitName = "bra")
@Table(schema = "PWS", name="session")
public class SessionEntity implements Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "nas_id")
    private String nasId;

    @Column
    private String device;

    @Column
    private String username;

    @Column
    private String ip;

    @Column
    private String mac;

    @Column(name = "start_date")
    private Date startDate;

    @Column
    private String password;

    @Column
    private String os;

    @Column
    private String version;

    @Column(name = "last_modified")
    private Date lastModified;

    @Override
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "device='" + device + '\'' +
                ", id=" + id +
                ", nasId='" + nasId + '\'' +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", startDate=" + startDate +
                ", password='" + password + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", lastModified=" + lastModified +
                '}';
    }
}
