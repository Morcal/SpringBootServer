package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;

import java.io.IOException;
import java.util.Collection;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public interface ServerHandler {
    /**
     * Handle challenge.
     * @param ip source ip.
     * @param requestId request id.
     * @param results results.
     * @return challenge error.
     */
    ChallengeError challenge(String ip, int requestId, Collection<String> results);

    /**
     * Handle incoming authentication request.
     * @param requestId request id.
     * @param credentials credentials.
     * @param authType authentication type.
     * @throws IOException
     */
    AuthError authenticate(int requestId, Credentials credentials, AuthType authType) throws IOException;

    /**
     * Handle incoming logout request.
     * @param credentials credentials.
     * @throws IOException
     */
    LogoutError logout(Credentials credentials) throws IOException;

    /**
     * Handle incoming logout request.
     * @param nasIp NAS/BRAS device ip.
     * @param userIp user ip.
     * @throws IOException
     */
    LogoutError ntfLogout(String nasIp, String userIp) throws IOException;
}
