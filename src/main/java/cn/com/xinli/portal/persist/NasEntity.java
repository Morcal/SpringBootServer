package cn.com.xinli.portal.persist;

import cn.com.xinli.portal.Nas;

import javax.persistence.*;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="nas")
public class NasEntity implements Nas {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** IPv4 Address. */
    @Column
    private String ipv4Address;

    /** IPv6 Address. */
    @Column
    private String ipv6Address;

    /** Nas type. */
    @Column
    private String type;

    /** Portal listen port. */
    @Column
    private int listenPort;

    /** Authentication type (PAP/CHAP). */
    @Column
    private String authType;

    /** IPv4 range start. */
    @Column
    private int ipv4start;

    /** IPv4 range end. */
    @Column
    private int ipv4end;

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    @Override
    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    @Override
    public int getIpv4end() {
        return ipv4end;
    }

    public void setIpv4end(int ipv4end) {
        this.ipv4end = ipv4end;
    }

    @Override
    public int getIpv4start() {
        return ipv4start;
    }

    public void setIpv4start(int ipv4start) {
        this.ipv4start = ipv4start;
    }

    @Override
    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    @Override
    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NasEntity{" +
                "authType='" + authType + '\'' +
                ", id=" + id +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv4end=" + ipv4end +
                ", ipv4start=" + ipv4start +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", listenPort=" + listenPort +
                ", type='" + type + '\'' +
                '}';
    }
}
