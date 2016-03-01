package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.huawei.nio.DatagramConnector;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;

/**
  * <p>Project: xpws
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
    public void testHuaweiPortalServer() throws IOException, TransportException {
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final Version version = Version.V2;
        final int port = 2003;
        final AuthType authType = AuthType.CHAP;
        final String sharedSecret = "aaa";
        final Endpoint endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        final DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        final String clientIp = "192.168.3.26";

        final PortalServer server = HuaweiPortal.createServer(endpoint, new PortalHandler());
        server.start();

        Packet ntfLogout = Packets.newNtfLogout(Version.V2, AuthType.CHAP, endpoint.getAddress(), clientIp, 1);
        Optional<Packet> response = client.request(ntfLogout);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isPresent());
        Packet packet = response.get();
        Assert.assertEquals(RequestType.ACK_NTF_LOGOUT.code(), packet.getType());

        server.shutdown();
    }

    @Test
    public void testHuaweiPortalServerWithPap() throws IOException, TransportException {
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final Version version = Version.V2;
        final int port = 2004;
        final AuthType authType = AuthType.PAP;
        final String sharedSecret = "aaa";
        final Endpoint endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        final DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        final String clientIp = "192.168.3.26";

        final PortalServer server = HuaweiPortal.createServer(endpoint, new PortalHandler());
        server.start();

        Packet ntfLogout = Packets.newNtfLogout(Version.V2, AuthType.PAP, endpoint.getAddress(), clientIp, 1);
        Optional<Packet> response = client.request(ntfLogout);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isPresent());
        Packet packet = response.get();
        Assert.assertEquals(RequestType.ACK_NTF_LOGOUT.code(), packet.getType());

        server.shutdown();
    }
}
