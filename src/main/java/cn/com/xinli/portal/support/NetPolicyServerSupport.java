package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.nps.NetPolicyServer;
import cn.com.xinli.nps.Result;
import cn.com.xinli.radius.RadiusEndpoint;
import org.springframework.stereotype.Component;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Component
public class NetPolicyServerSupport implements NetPolicyServer {
    @Override
    public Result authenticate(Credentials credentials) {
        return null;
    }

    @Override
    public Result logout(Credentials credentials) {
        return null;
    }

    @Override
    public Result authorize(Nas nas, Credentials credentials) {
        return null;
    }

    @Override
    public Result authenticate(RadiusEndpoint server, Credentials credentials) {
        return null;
    }

    @Override
    public Result account(RadiusEndpoint server, Credentials credentials) {
        return null;
    }
}
