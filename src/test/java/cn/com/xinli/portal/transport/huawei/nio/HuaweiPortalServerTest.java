package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/10.
 */
public class HuaweiPortalServerTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPortalServerTest.class);

    class PortalHandler implements ServerHandler {
        @Override
        public ChallengeError challenge(String ip, int requestId, Collection<String> results) {
            return null;
        }

        @Override
        public AuthError authenticate(int requestId, Credentials credentials, AuthType authType) throws IOException {
            return null;
        }

        @Override
        public LogoutError logout(Credentials credentials) throws IOException {
            return null;
        }

        @Override
        public LogoutError ntfLogout(String nasIp, String userIp) throws IOException {
            logger.debug("NTF_LOGOUT nas: {}, user: {}", nasIp, userIp);
            return LogoutError.OK;
        }
    }

    @Test
    public void testHuaweiPortalServer() throws IOException, PortalProtocolException {
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final Version version = Version.V2;
        final int port = 2003;
        final AuthType authType = AuthType.CHAP;
        final String sharedSecret = "aaa";
        final Endpoint endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        final DefaultPortalClient client = (DefaultPortalClient) HuaweiPortal.createClient(endpoint);
        final String clientIp = "192.168.3.26";
        final byte[] ip = Packets.getIp4Address(clientIp);

        final PortalServer server = HuaweiPortal.createServer(endpoint, new PortalHandler());
        server.start();

        HuaweiPacket ntfLogout = Packets.newNtfLogout(Version.V2, AuthType.CHAP, endpoint.getAddress(), ip, 1);
        Optional<HuaweiPacket> response = client.request(ntfLogout);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isPresent());
        HuaweiPacket huaweiPacket = response.get();
        Assert.assertEquals(RequestType.ACK_NTF_LOGOUT.code(), huaweiPacket.getType());

        server.shutdown();
    }

    @Test
    public void testHuaweiPortalServerWithPap() throws IOException, PortalProtocolException {
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final Version version = Version.V2;
        final int port = 2004;
        final AuthType authType = AuthType.PAP;
        final String sharedSecret = "aaa";
        final Endpoint endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        final DefaultPortalClient client = (DefaultPortalClient) HuaweiPortal.createClient(endpoint);
        final String clientIp = "192.168.3.26";
        final byte[] ip = Packets.getIp4Address(clientIp);

        final PortalServer server = HuaweiPortal.createServer(endpoint, new PortalHandler());
        server.start();

        HuaweiPacket ntfLogout = Packets.newNtfLogout(Version.V2, AuthType.PAP, endpoint.getAddress(), ip, 1);
        Optional<HuaweiPacket> response = client.request(ntfLogout);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isPresent());
        HuaweiPacket huaweiPacket = response.get();
        Assert.assertEquals(RequestType.ACK_NTF_LOGOUT.code(), huaweiPacket.getType());

        server.shutdown();
    }
}
