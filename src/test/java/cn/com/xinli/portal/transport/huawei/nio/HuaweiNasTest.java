package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.DefaultCredentials;
import cn.com.xinli.portal.core.credentials.HuaweiCredentials;
import cn.com.xinli.portal.transport.*;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Mock HUAWEI NAS test.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/27.
 */
public class HuaweiNasTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiNasTest.class);

    final int CONCURRENT_SIZE = 10, RUN_TIMES = 10;

    InetAddress address;
    Version version;
    int port;
    AuthType authType;
    String sharedSecret;
    Endpoint endpoint;
    HuaweiCredentials credentials;
    PortalServer server;

    final ExecutorService executorService = Executors.newCachedThreadPool();

    @Before
    public void setup() throws TransportException, UnknownHostException {
        address = InetAddress.getByName("127.0.0.1");
        version = Version.V2;
        port = 2003;
        authType = AuthType.CHAP;
        sharedSecret = "aaa";
        endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        credentials = HuaweiCredentials.of("test0", "test0", "127.0.0.1", "mac", 0);
    }

    @After
    public void tearDown() throws InterruptedException {
        if (server != null)
            server.shutdown();

        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }


    @Test
    public void testHuaweiNas() throws IOException, InterruptedException, TransportException {
        endpoint.setPort(2000);
        logger.debug("endpoint: {}", endpoint);

        PortalServer server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);

        final PortalClient client = HuaweiPortal.createClient(endpoint);
        Result response = client.login(credentials);
        Assert.assertNotNull(response);

        response = client.logout(credentials);
        Assert.assertNotNull(response);

        server.shutdown();
    }

    @Test
    public void testLoginMoreThanOnce() throws IOException, InterruptedException, TransportException {
        endpoint.setPort(2004);
        logger.debug("endpoint: {}", endpoint);

        server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);
        final PortalClient client = HuaweiPortal.createClient(endpoint);
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

    private void concurrentRun(final Credentials credentials) throws IOException, TransportException {
        final PortalClient client = HuaweiPortal.createClient(endpoint);
        for (int i = 0; i < RUN_TIMES; i ++) {
            Result response = client.login(credentials);
            Assert.assertNotNull(response);

            response = client.logout(credentials);
            Assert.assertNotNull(response);
        }
    }

    public void concurrentAccess() {
        for (int i = 0; i < CONCURRENT_SIZE; i++) {
            final Credentials credentials = DefaultCredentials.of("test" + i, "test" + i, "192.168.3." + i, "mac-" + i);
            executorService.submit(() -> {
                try {
                    this.concurrentRun(credentials);
                } catch (IOException | TransportException e) {
                    logger.error("Concurrent access error", e);
                }
            });
        }

    }

    @Test
    public void testConcurrentAccess() throws IOException, InterruptedException {
        endpoint.setPort(2002);
        logger.debug("endpoint: {}", endpoint);

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
}
