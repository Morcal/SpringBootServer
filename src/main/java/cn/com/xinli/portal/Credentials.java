package cn.com.xinli.portal;

/**
 * Portal user credentials.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class Credentials {
    /** user name. */
    private final String username;

    /** user password. */
    private final String password;

    /** User ip address. */
    private final String ip;

    /** User mac address. */
    private final String mac;

    public Credentials(String username, String password, String ip, String mac) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
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
