package cn.com.xinli.portal.protocol;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class Credentials {
    private final String username;
    private final String password;
    private final String ip;
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
}
