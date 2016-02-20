package cn.com.xinli.portal.core.credentials;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * Portal user credentials.
 *
 * <p>In a portal service, {@link #username} and {@link #password} are provides for
 * AAA platform. {@link #ip} is essential information for NAS/BRAS devices to
 * create/destroy broadband connections. {@link #mac} is optional for devices and
 * AAA platform, but it may be critical for certain systems. PWS REST service requires
 * clients to provide MAC address.
 *
 * <p>Credentials contains all information needed for a
 * more generic portal service, and it's sufficient for user to
 * perform an authentication via web page.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
@Entity
@PersistenceUnit(unitName = "bra")
@Table(schema = "PWS", name="credentials")
@JsonInclude
public class Credentials {
    /** Empty credentials error message. */
    public static final String EMPTY_CREDENTIALS = "Credentials is empty";

    /** Id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    /** user name. */
    @Column(nullable = false)
    @JsonProperty
    private String username;

    /** user password. */
    @Column
    @JsonProperty
    private String password;

    /** User ip address. */
    @Column(nullable = false)
    @JsonProperty
    private String ip;

    /** User mac address. */
    @Column
    @JsonProperty
    private String mac;

    /**
     * Get session user ip address.
     * <p>Normally, user's ip address will never be null.</p>
     * @return session user ip address.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Get session user mac address if presents.
     * @return user's mac or null.
     */
    public String getMac() {
        return mac;
    }

    /**
     * Get session user password.
     * @return session user password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get session user name.
     * @return session user name.
     */
    public String getUsername() {
        return username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    /**
     * Create credentials.
     * @param username username.
     * @param password password.
     * @param ip ip address.
     * @param mac mac address.
     * @return credentials.
     */
    public static Credentials of(String username, String password, String ip, String mac) {
        Credentials credentials = new Credentials();
        credentials.setUsername(username);
        credentials.setPassword(password);
        credentials.setIp(ip);
        credentials.setMac(mac);
        return credentials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Credentials that = (Credentials) o;

        return username.equals(that.username) &&
                password.equals(that.password) &&
                ip.equals(that.ip) &&
                (mac != null ? mac.equals(that.mac) : that.mac == null);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + ip.hashCode();
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Credentials{" +
                "ip='" + ip + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
