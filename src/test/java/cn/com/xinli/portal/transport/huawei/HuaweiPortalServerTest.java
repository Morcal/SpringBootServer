package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.AuthType;
import cn.com.xinli.portal.transport.Packet;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.PortalServerConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/10.
 */
public class HuaweiPortalServerTest extends HuaweiTestBase {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPortalServerTest.class);

    PortalServer server;

    class PortalHandler implements PortalServerHandler {
        @Override
        public LogoutError handleNtfLogout(String address) {
            logger.debug("NTF_LOGOUT {}", address);
            return LogoutError.OK;
        }
    }

    @Before
    public void setup() throws PortalProtocolException {
        nas = createNas(2003);
        client = createPortalClient(nas);
        credentials = createCredentials();
    }

    @After
    public void tearDown() throws InterruptedException {
        if (server != null)
            server.shutdown();
        super.tearDown();
    }

    @Test
    public void testHuaweiPortalServer() throws IOException, PortalProtocolException {
        PortalServerConfig config = new PortalServerConfig();
        config.setListenPort(nas.getListenPort());
        config.setSharedSecret(nas.getSharedSecret());
        config.setThreadSize(4);

        final String clientIp = "192.168.3.26";
        final byte[] ip = Packets.getIp4Address(clientIp);

        server = HuaweiPortal.createServer(config, new PortalHandler());
        server.start();

        HuaweiPacket ntfLogout = Packets.newNtfLogout(new V2(), AuthType.CHAP, nas.getNetworkAddress(), ip, 1);
        Optional<Packet> response = client.request(ntfLogout);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isPresent());
        HuaweiPacket huaweiPacket = (HuaweiPacket) response.get();
        Assert.assertEquals(RequestType.ACK_NTF_LOGOUT.code(), huaweiPacket.getType());
    }

}
