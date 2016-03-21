package cn.com.xinli.radius.support;

import cn.com.xinli.radius.Credentials;
import cn.com.xinli.radius.RadiusClient;
import cn.com.xinli.radius.RadiusCodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class DefaultRadiusClient implements RadiusClient {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DefaultRadiusClient.class);

    private String host;
    private int port;
    private String sharedSecret;

    private RadiusCodecFactory codecFactory;


    @Override
    public void authenticate(Credentials credentials) {

    }

    @Override
    public void logout(Credentials credentials) {

    }

    @Override
    public void account(Credentials credentials) {

    }
}
