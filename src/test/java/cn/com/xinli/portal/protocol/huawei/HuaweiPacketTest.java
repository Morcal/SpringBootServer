package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.TestBase;
import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.support.NasConfiguration;
import cn.com.xinli.portal.support.NasSupport;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class HuaweiPacketTest extends TestBase {

    final Credentials credentials = new Credentials("zhoup", "123456", "192.168.3.26", "20cf-30bb-e9af");

    Nas createNas() {
        NasConfiguration config = new NasConfiguration();
        config.setAuthType("PAP");
        config.setId(1);
        config.setIpv4Address("129.168.3.95");
        config.setIpv6Address("");
        config.setListenPort(2000);
        config.setName("h3c-vbras");
        config.setSharedSecret("aaa");
        config.setType("Huawei v2");
        return NasSupport.build(config);
    }

    @Test
    public void testAuthenticator() throws IOException {
        Nas nas = createNas();
        //PortalClient client = PortalClients.create(createNas());
        CodecFactory codecFactory = new HuaweiCodecFactory();

        DefaultPortalClient client = new DefaultPortalClient(nas, new V2());
        HuaweiPacket papAuth = (HuaweiPacket) client.createPapAuthPacket(credentials);
        DatagramPacket packet = codecFactory.getEncoder()
                .encode(papAuth,
                        nas.getInetAddress(),
                        nas.getListenPort(),
                        nas.getSharedSecret());
        HuaweiPacket decoded = (HuaweiPacket) codecFactory.getDecoder().decode(packet, nas.getSharedSecret());

        Assert.assertNotNull(decoded);
        Assert.assertTrue(Arrays.equals(papAuth.getAuthenticator(), decoded.getAuthenticator()));
        log.debug(papAuth);
        log.debug(decoded);
    }
}