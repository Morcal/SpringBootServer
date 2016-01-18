package cn.com.xinli.portal.support;

import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.NasType;
import cn.com.xinli.portal.repository.NasEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public class NasAdapterTest {
    NasEntity nasEntity;


    @Before
    public void setup() {
        nasEntity = new NasEntity();
        nasEntity.setAuthType(AuthType.CHAP);
        nasEntity.setId(1);
        nasEntity.setIpv4Address("192.168.3.26");
        nasEntity.setListenPort(2000);
        nasEntity.setNasId("test-nas");
        nasEntity.setNasId("xinli-test-nas-01");
        nasEntity.setSharedSecret("s3cr3t");
        nasEntity.setSupportedDomains("@xinli,@shaanxi");
        nasEntity.setType(NasType.HuaweiV2);
        nasEntity.setIpv4start("192.168.3.1");
        nasEntity.setIpv4end("192.168.3.254");
    }

    @Test
    public void testNasAdapter() {
        NasAdapter adapter = new NasAdapter(nasEntity);

        Assert.assertTrue(adapter.contains("192.168.3.25"));
        Assert.assertTrue(adapter.containsDomain("@xinli"));
        Assert.assertTrue(adapter.containsDomain("@shaanxi"));
    }
}
