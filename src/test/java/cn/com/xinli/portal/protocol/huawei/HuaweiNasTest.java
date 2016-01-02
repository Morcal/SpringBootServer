package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.Credentials;
import cn.com.xinli.portal.persist.NasEntity;
import cn.com.xinli.portal.protocol.PortalClient;
import cn.com.xinli.portal.protocol.support.PortalClients;
import cn.com.xinli.portal.support.NasAdapter;
import cn.com.xinli.portal.util.AddressUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class HuaweiNasTest extends TestBase {
    Nas nas = null;
    PortalClient client;
    Credentials credentials;
    final int CONCURRENT_SIZE = 10, RUN_TIMES = 1000;
    ExecutorService executorService;

    @Before
    public void createNas() {
        credentials = new Credentials("test0", "test0", "127.0.0.1", "mac");
        NasEntity entity = new NasEntity();
        entity.setType(NasType.HuaweiV2);
        entity.setSharedSecret("aaa");
        entity.setName("Test Nas");
        entity.setIpv4Address("127.0.0.1");
        entity.setListenPort(2000);
        entity.setAuthType(AuthType.CHAP);
        entity.setId(1);
        entity.setIpv4start(AddressUtil.convertIpv4Address("192.168.3.1"));
        entity.setIpv4end(AddressUtil.convertIpv4Address("192.168.3.254"));
        entity.setNasId("test-01");
        nas = new NasAdapter(entity);

        client = PortalClients.create(nas);
        executorService = Executors.newCachedThreadPool();
    }

    @Test
    public void testHuaweiNas() throws IOException, InterruptedException {
        HuaweiNas server = new HuaweiNas(this.nas);
        server.start();

        //Thread.sleep(100L);

        Message<?> response = client.login(credentials);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());

        response = client.logout(credentials);
        Assert.assertNotNull(response);
        Assert.assertTrue(response.isSuccess());

        server.shutdown();
    }

    private void concurrentRun(final Credentials credentials) throws IOException {
        for (int i = 0; i < RUN_TIMES; i ++) {
            Message<?> response = client.login(credentials);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());

            response = client.logout(credentials);
            Assert.assertNotNull(response);
            Assert.assertTrue(response.isSuccess());
        }
    }

    public void concurrentAccess() {
        for (int i = 0; i < CONCURRENT_SIZE; i++) {
            final Credentials credentials = new Credentials("test" + i, "test" + i, "192.168.3." + i, "mac-" + i);
            executorService.submit(() -> {
                try {
                    this.concurrentRun(credentials);
                } catch (IOException e) {
                    logger.error("Concurrent access error", e);
                }
            });
        }

    }

    @Test
    public void testConcurrentAccess() throws IOException, InterruptedException {
        HuaweiNas server = new HuaweiNas(this.nas);
        server.start();

        long now = System.currentTimeMillis();
        concurrentAccess();

        executorService.shutdown();
        executorService.awaitTermination(10L, TimeUnit.SECONDS);

        now = System.currentTimeMillis() - now;
        logger.warn("Testing run in {} threads, with {} times each, cost {} milliseconds.",
                CONCURRENT_SIZE, RUN_TIMES, now);


        server.shutdown();
    }
}
