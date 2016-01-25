package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.AuthType;
import cn.com.xinli.portal.core.Credentials;
import cn.com.xinli.portal.core.Nas;
import cn.com.xinli.portal.core.NasType;
import cn.com.xinli.portal.transport.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Huawei Packet Test.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class HuaweiPacketTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPacketTest.class);

    Nas nas;
    Credentials credentials;
    HuaweiCodecFactory codecFactory;

    @Before
    public void setup() {
        credentials = new Credentials("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af");
        nas.setAuthType(AuthType.CHAP);
        nas.setId(1);
        nas.setIpv4Address("129.168.3.95");
        nas.setIpv6Address("");
        nas.setListenPort(2000);
        nas.setName("h3c-vbras");
        nas.setSharedSecret("aaa");
        nas.setType(NasType.HuaweiV2);
//        nas.setIpv4start("10.48.47.1");
//        nas.setIpv4end("10.48.47.254");
        logger.debug("nas: {}", nas);
        codecFactory = new HuaweiCodecFactory();
    }

    @Test
    public void testPapAuth() throws IOException, PortalProtocolException {
        DefaultPortalClient client = new DefaultPortalClient(nas, new V2());
        HuaweiPacket papAuth = client.createPapAuthPacket(credentials);
        ByteBuffer buffer = codecFactory.getEncoder().encode(papAuth, nas.getSharedSecret());
        buffer.rewind();
        HuaweiPacket decoded = codecFactory.getDecoder().decode(buffer, nas.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(papAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(papAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }

    @Test
    public void testChapAuth() throws IOException, PortalProtocolException {
        DefaultPortalClient client = new DefaultPortalClient(nas, new V2());
        HuaweiPacket chapReq = Packets.newChapReq(new V1(), credentials);
        HuaweiPacket chapAck = Packets.newChallengeAck(
                InetAddress.getLocalHost(), "challenge-value", 1, ChallengeError.OK, chapReq);
        HuaweiPacket chapAuth = client.createChapAuthPacket(chapAck, credentials);
        ByteBuffer buffer = codecFactory.getEncoder().encode(chapAuth, nas.getSharedSecret());
        buffer.rewind();
        HuaweiPacket decoded = codecFactory.getDecoder().decode(buffer, nas.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(chapAuth.getAuthenticator(), decoded.getAuthenticator()));

        Assert.assertEquals(chapAuth.toString(), decoded.toString());
        logger.debug("decoded: {}", decoded);
    }
}
