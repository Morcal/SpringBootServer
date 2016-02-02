package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.HuaweiCredentials;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.huawei.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * HUAWEI Packet Test.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class HuaweiPacketTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPacketTest.class);

    Endpoint endpoint;
    HuaweiCredentials credentials;
    HuaweiCodecFactory codecFactory;

    @Before
    public void setup() throws UnknownHostException {
        credentials = HuaweiCredentials.of("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af", 0);
        endpoint = new Endpoint();
        endpoint.setVersion(Version.V2);
        endpoint.setAddress(InetAddress.getByName("127.0.0.1"));
        endpoint.setPort(2000);
        endpoint.setSharedSecret("aaa");
        logger.debug("endpoint: {}", endpoint);
        codecFactory = new HuaweiCodecFactory();
    }

    @Test
    public void testPapAuth() throws IOException, PortalProtocolException {
        endpoint.setAuthType(AuthType.PAP);
        DefaultPortalClient client = (DefaultPortalClient) HuaweiPortal.createClient(endpoint);
        //HuaweiPacket papAuth = client.createPapAuthPacket(credentials);
        HuaweiPacket papAuth = client.createRequest(RequestType.REQ_AUTH, credentials, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(papAuth, endpoint.getSharedSecret());
        buffer.rewind();
        HuaweiPacket decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(papAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(papAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }

    @Test
    public void testChapAuth() throws IOException, PortalProtocolException {
        endpoint.setAuthType(AuthType.CHAP);
        DefaultPortalClient client = (DefaultPortalClient) HuaweiPortal.createClient(endpoint);
        HuaweiPacket chapReq = Packets.newChapReq(Version.V2, credentials, 1);
        HuaweiPacket chapAck = Packets.newChallengeAck(
                InetAddress.getLocalHost(), "challenge-value", 1, ChallengeError.OK, chapReq);
        HuaweiPacket chapAuth = client.createRequest(RequestType.REQ_AUTH, credentials, chapAck, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(chapAuth, endpoint.getSharedSecret());
        buffer.rewind();
        HuaweiPacket decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(chapAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(chapAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }
}
