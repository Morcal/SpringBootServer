package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.huawei.nio.ByteBufferCodecFactory;
import cn.com.xinli.portal.transport.huawei.nio.DatagramConnector;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
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
public class PacketTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PacketTest.class);

    Endpoint endpoint;
    Credentials credentials;
    ByteBufferCodecFactory codecFactory;

    @Before
    public void setup() throws UnknownHostException {
        credentials = Credentials.of("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af");
        endpoint = new Endpoint();
        endpoint.setVersion(Version.V2);
        endpoint.setAddress(InetAddress.getByName("127.0.0.1"));
        endpoint.setPort(2000);
        endpoint.setSharedSecret("aaa");
        logger.debug("endpoint: {}", endpoint);
        codecFactory = new ByteBufferCodecFactory();
    }

    @Test
    public void testPapAuth() throws IOException, TransportException {
        endpoint.setAuthType(AuthType.PAP);
        DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        //Packet papAuth = client.createPapAuthPacket(credentials);
        Packet papAuth = client.createRequest(RequestType.REQ_AUTH, credentials, null, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(papAuth, endpoint.getSharedSecret());
        buffer.rewind();
        Packet decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(papAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(papAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }

    @Test
    public void testChapAuth() throws IOException, TransportException {
        endpoint.setAuthType(AuthType.CHAP);
        DatagramConnector client = (DatagramConnector) HuaweiPortal.getConnector(endpoint);
        Packet chapReq = Packets.newChapReq(Version.V2, credentials, 1);
        Packet chapAck = Packets.newChallengeAck(
                InetAddress.getLocalHost(), "challenge-value", 1, ChallengeError.OK, chapReq);
        Packet chapAuth = client.createRequest(RequestType.REQ_AUTH, credentials, chapAck, null, 1);
        ByteBuffer buffer = codecFactory.getEncoder().encode(chapAuth, endpoint.getSharedSecret());
        buffer.rewind();
        Packet decoded = codecFactory.getDecoder().decode(buffer, endpoint.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(chapAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(chapAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }
}
