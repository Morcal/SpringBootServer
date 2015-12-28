package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.AuthType;
import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.NasType;
import cn.com.xinli.portal.TestBase;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.support.NasConfiguration;
import cn.com.xinli.portal.support.NasSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class HuaweiPacketTest extends TestBase {
    Nas nas;
    Credentials credentials;
    HuaweiCodecFactory codecFactory;

    @Before
    public void setup() {
        credentials = new Credentials("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af");
        NasConfiguration config = new NasConfiguration();
        config.setAuthType(AuthType.CHAP.name());
        config.setId(1);
        config.setIpv4Address("129.168.3.95");
        config.setIpv6Address("");
        config.setListenPort(2000);
        config.setName("h3c-vbras");
        config.setSharedSecret("aaa");
        config.setType(NasType.HuaweiV2.name());
        nas = NasSupport.build(config);
        codecFactory = new HuaweiCodecFactory();
    }

    @Test
    public void testPapAuth() throws IOException {
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
    public void testChapAuth() throws IOException {
        DefaultPortalClient client = new DefaultPortalClient(nas, new V2());
        HuaweiPacket chapReq = Packets.newChapReq(new V1(), credentials);
        HuaweiPacket chapAck = Packets.newChallengeAck(
                InetAddress.getLocalHost(), "challenge-value", 1, Enums.ChallengeError.OK, chapReq);
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
