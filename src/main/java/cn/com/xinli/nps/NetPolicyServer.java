package cn.com.xinli.nps;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.radius.RadiusEndpoint;

/**
 * Net policy server.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public interface NetPolicyServer {
    Result authenticate(Credentials credentials);
    Result logout(Credentials credentials);
    Result authorize(Nas nas, Credentials credentials);
    Result authenticate(RadiusEndpoint server, Credentials credentials);
    Result account(RadiusEndpoint server, Credentials credentials);
}
