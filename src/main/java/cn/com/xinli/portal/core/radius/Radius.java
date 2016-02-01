package cn.com.xinli.portal.core.radius;

import javax.persistence.*;

/**
 * Portal web server remote radius supporting RADIUS portal.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
@Entity
@PersistenceUnit(unitName = "system")
@Table(schema = "PWS", name="radius")
public class Radius {
    /** Auto generated internal id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    /** RADIUS endpoint address. */
    @Column(nullable = false)
    private String address;

    /** RADIUS endpoint port. */
    @Column(nullable = false)
    private int port;

    /** ISP defined human-readable radius name. */
    @Column(nullable = false)
    private String name;

    /** RADIUS endpoint shared secret. */
    @Column(name = "shared_secret", nullable = false)
    private String sharedSecret;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    public String toString() {
        return "Radius{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                ", sharedSecret='" + sharedSecret + '\'' +
                '}';
    }
}
