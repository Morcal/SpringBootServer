package cn.com.xinli.portal.protocol;

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
    public String toString() {
        return "Credentials{" +
                "ip='" + ip + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mac='" + mac + '\'' +
                '}';
    }
}
