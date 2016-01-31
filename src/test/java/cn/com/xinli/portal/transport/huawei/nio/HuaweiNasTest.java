package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.ChallengeException;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.Result;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Mock HUAWEI NAS test.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class HuaweiNasTest extends HuaweiTestBase {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiNasTest.class);

    final int CONCURRENT_SIZE = 10, RUN_TIMES = 10, LISTEN_PORT = 2001;
    PortalServer server;

    @Before
    public void setup() throws PortalProtocolException {
        credentials = createCredentials();
        client = createPortalClient(createEndpoint(LISTEN_PORT));
        endpoint = createEndpoint(LISTEN_PORT);
    }

    @After
    public void tearDown() throws InterruptedException {
        if (server != null)
            server.shutdown();
        super.tearDown();
    }


    @Test
    public void testHuaweiNas() throws IOException, InterruptedException, PortalProtocolException {
        //HuaweiNas server = new HuaweiNas(this.nas);
        server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);

        Result response = client.login(credentials);
        Assert.assertNotNull(response);

        response = client.logout(credentials);
        Assert.assertNotNull(response);
    }

    @Test
    public void testLoginMoreThanOnce() throws IOException, InterruptedException, PortalProtocolException {
        //HuaweiNas server = new HuaweiNas(this.nas);
        endpoint.setPort(2004);
        server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);

        Result response = client.login(credentials);
        Assert.assertNotNull(response);

        response = null;
        try {
            response = client.login(credentials);
        } catch (ChallengeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("challenge exception: {}", e.getMessage());
            }
        }

        Assert.assertNull(response);
    }

    private void concurrentRun(final Credentials credentials) throws IOException, PortalProtocolException {
        for (int i = 0; i < RUN_TIMES; i ++) {
            Result response = client.login(credentials);
            Assert.assertNotNull(response);

            response = client.logout(credentials);
            Assert.assertNotNull(response);
        }
    }

    public void concurrentAccess() {
        for (int i = 0; i < CONCURRENT_SIZE; i++) {
            final Credentials credentials = Credentials.of("test" + i, "test" + i, "192.168.3." + i, "mac-" + i);
            executorService.submit(() -> {
                try {
                    this.concurrentRun(credentials);
                } catch (IOException | PortalProtocolException e) {
                    logger.error("Concurrent access error", e);
                }
            });
        }

    }

    @Test
    public void testConcurrentAccess() throws IOException, InterruptedException {
        //HuaweiNas server = new HuaweiNas(this.nas);
        endpoint.setPort(2002);
        server = HuaweiPortal.createNas(endpoint);
        server.start();

        long now = System.currentTimeMillis();
        concurrentAccess();

        executorService.shutdown();
        executorService.awaitTermination(10L, TimeUnit.SECONDS);

        now = System.currentTimeMillis() - now;
        logger.warn("Testing run in {} threads, with {} times each, cost {} milliseconds.",
                CONCURRENT_SIZE, RUN_TIMES, now);
    }

//    @Test
//    public void testAuthenticateWithDomain() {
//        Assert.assertTrue(nas.authenticateWithDomain());
//    }
}
