package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.ChallengeException;
import cn.com.xinli.portal.transport.Connector;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
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
    Credentials credentials;
    PortalServer server;

    final ExecutorService executorService = Executors.newCachedThreadPool();

    @Before
    public void setup() throws TransportException, UnknownHostException {
        address = InetAddress.getByName("127.0.0.1");
        version = Version.V2;
        port = 2003;
        authType = AuthType.CHAP;
        authType = AuthType.PAP;
        sharedSecret = "aaa";
        endpoint = Endpoint.of(version, address, port, authType, sharedSecret);
        credentials = Credentials.of("test0", "test0", "127.0.0.1", "mac");
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
        endpoint.setPort(2010);
        logger.debug("endpoint: {}", endpoint);

        PortalServer server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);

        final Connector<RequestContext> client = HuaweiPortal.getConnector(endpoint);
        RequestContext extendedInformation = client.login(credentials);

        Assert.assertNotNull(extendedInformation);

        client.logout(credentials, extendedInformation);

        server.shutdown();
    }

    @Test
    public void testLoginMoreThanOnce() throws IOException, InterruptedException, TransportException {
        endpoint.setPort(2004);
        logger.debug("endpoint: {}", endpoint);

        server = HuaweiPortal.createNas(endpoint);
        server.start();

        //Thread.sleep(100L);
        final Connector<RequestContext> client = HuaweiPortal.getConnector(endpoint);
        RequestContext context = client.login(credentials);
        Assert.assertNotNull(context);

        try {
            client.login(credentials);
            Assert.assertTrue(true);
        } catch (ChallengeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("challenge exception: {}", e.getMessage());
            }
        }
    }

    private void concurrentRun(final Credentials credentials) throws IOException, TransportException {
        final Connector<RequestContext> client = HuaweiPortal.getConnector(endpoint);
        for (int i = 0; i < RUN_TIMES; i ++) {
            RequestContext context = client.login(credentials);
            Assert.assertNotNull(context);

            client.logout(credentials, context);
        }
    }

    public void concurrentAccess() {
        for (int i = 0; i < CONCURRENT_SIZE; i++) {
            final Credentials credentials = Credentials.of("test" + i, "test" + i, "192.168.3." + i, "mac-" + i);
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
