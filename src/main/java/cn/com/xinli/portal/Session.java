package cn.com.xinli.portal;

import javax.persistence.*;
import java.util.Date;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
@Entity
@Table(schema = "PWS", name="Session")
@NamedQueries(value = {
        @NamedQuery(name = "Session.findByUsername", query = "from Session s where s.username = ?1"),
        @NamedQuery(name = "Session.findByIpAndMac", query = "from Session s where s.ip = ?1 and s.mac= ?2"),
})
public class Session {

    @Column(unique = true)
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
