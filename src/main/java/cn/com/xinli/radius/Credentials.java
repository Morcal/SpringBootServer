package cn.com.xinli.radius;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class Credentials {
    private final String username;
    private final String password;

    public Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
