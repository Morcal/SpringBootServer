package cn.com.xinli.radius.support;

import cn.com.xinli.radius.RadiusClient;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusClients {

    public static RadiusClient newClient() {
        return new DefaultRadiusClient();
    }
}
