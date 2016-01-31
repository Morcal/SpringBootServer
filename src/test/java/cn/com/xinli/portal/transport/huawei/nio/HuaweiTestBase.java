package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/10.
 */
public abstract class HuaweiTestBase {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiTestBase.class);

    protected Endpoint endpoint = null;
    protected DefaultPortalClient client;
    protected Credentials credentials;
    protected ExecutorService executorService;

    public HuaweiTestBase() {
        executorService = Executors.newCachedThreadPool();
    }

    protected Credentials createCredentials() {
        return Credentials.of("test0", "test0", "127.0.0.1", "mac");
    }

    protected DefaultPortalClient createPortalClient(Endpoint endpoint) throws PortalProtocolException {
        return (DefaultPortalClient) HuaweiPortal.createClient(endpoint);
    }

    protected Endpoint createEndpoint(int port) {
        endpoint = new Endpoint();
        endpoint.setPort(port);
        endpoint.setAuthType(AuthType.CHAP);
        endpoint.setSharedSecret("aaa");
        logger.debug("endpoint: {}", endpoint);
        return endpoint;
    }

//    protected Nas create(int port) throws PortalProtocolException {
//        nas.setType(NasType.HuaweiV2);
//        nas.setSharedSecret("aaa");
//        nas.setName("test-01");
//        nas.setIpv4Address("127.0.0.1");
//        nas.setListenPort(port);
//        nas.setAuthType(AuthType.CHAP);
//        nas.setId(1);
////        nas.setIpv4start("192.168.3.1");
////        nas.setIpv4end("192.168.3.254");
////        nas.setSupportedDomains("@xinli,@hubei,@hebei");
////        nas.setAuthenticateWithDomain(true);
//        logger.debug("nas: {}", nas);
//        return nas;
//    }

    protected void tearDown() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
        executorService.shutdownNow();
    }
}
