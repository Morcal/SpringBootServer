package cn.com.xinli.portal.core;

import javax.persistence.*;
import java.util.Date;

/**
 * PWS portal session.
 *
 * <p>This class instances represent portal authentication based
 * broadband internet connections.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
@Entity
@PersistenceUnit(unitName = "bra")
@Table(schema = "PWS", name="session")
public class DbSession implements Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name = "nas_id", referencedColumnName = "id")
    private Nas nas;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String ip;

    @Column
    private String mac;

    @Column(name = "start_date")
    private Date startTime;

    @ManyToOne
    @JoinColumn(name = "certificate_id", referencedColumnName = "id")
    private Certificate certificate;

    /** Last modified time (UNIX epoch time), do not save in database. */
    @Transient
    private long lastModified = 0L;

    /**
     * Get session internal id.
     *
     * @return session internal id.
     */
    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Nas getNas() {
        return nas;
    }

    public void setNas(Nas nas) {
        this.nas = nas;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    /**
     * Get session user name.
     * @return session user name.
     */
    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get session user ip address.
     * <p>Normally, user's ip address will never be null.</p>
     * @return session user ip address.
     */
    @Override
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Get session user mac address if presents.
     * @return user's mac or null.
     */
    @Override
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * Get Session start date.
     *
     * @return session start date.
     */
    @Override
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Get session last modified time (UNIX epoch time).
     * @return last modified time (UNIX epoch time).
     */
    @Override
    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long modified) {
        this.lastModified = modified;
    }

    @Override
    public String getAppName() {
        return certificate.getAppId();
    }

    @Override
    public String getNasName() {
        return nas.getName();
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", nas=" + nas.toString() +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", startTime=" + startTime +
                '}';
    }
}
