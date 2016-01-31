package cn.com.xinli.radius;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public interface RadiusClient {
    void authenticate(Credentials credentials);

    void logout(Credentials credentials);

    void account(Credentials credentials);
}
