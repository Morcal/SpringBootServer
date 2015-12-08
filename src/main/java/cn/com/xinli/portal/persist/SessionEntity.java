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
@Table(schema = "PWS", name="Session")
@NamedQueries(value = {
        @NamedQuery(name = "Session.findByUsername", query = "from SessionEntity s where s.username = ?1"),
        @NamedQuery(name = "Session.findByIpAndMac", query = "from SessionEntity s where s.ip = ?1 and s.mac= ?2"),
})
public class SessionEntity implements Session {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "nas_id")
    private String nasId;

    @Column
    private String username;

    @Column
    private String ip;

    @Column
    private String mac;

    @Column(name = "start_date")
    private Date startDate;

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

    @Override
    public String toString() {
        return "Session{" +
                "id='" + id + '\'' +
                ", nasId='" + nasId + '\'' +
                ", username='" + username + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
