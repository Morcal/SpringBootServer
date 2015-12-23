package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.protocol.Packet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Default portal server.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
public class DefaultPortalServer {
    /** Log. */
    private static final Log log = LogFactory.getLog(DefaultPortalServer.class);

    /** Default listen port. */
    private static final int DEFAULT_LISTEN_PORT = 2000;

    /** Default worker thread size. */
    private static final int DEFAULT_THREAD_SIZE = 4;

    private ExecutorService executorService;

    DatagramSocket socket;

    private volatile boolean shutdown = false;

    HuaweiCodecFactory codecFactory;

    protected final SessionService sessionService;

    protected final String sharedSecret;

    protected final int port;

    public DefaultPortalServer(ServerConfig serverConfig, SessionService sessionService) {
        this.sessionService = sessionService;
        codecFactory = new HuaweiCodecFactory();
        executorService = Executors.newFixedThreadPool(
                serverConfig.getPortalServerThreadSize() > 0 ?
                        serverConfig.getPortalServerThreadSize() : DEFAULT_THREAD_SIZE);
        this.sharedSecret = serverConfig.getPortalServerSharedSecret();
        this.port = serverConfig.getPortalServerListenPort();
    }

    protected void handlePacket(DatagramPacket packet) {
        try {
            if (codecFactory.verify(packet, sharedSecret)) {
                Packet in = codecFactory.getDecoder().decode(packet, sharedSecret);
                byte[] ip = in.getIp();
                //byte[] mac = in.getAttribute(Enums.Attribute.USER_MAC);
                String addr = InetAddress.getByAddress(ip).getHostAddress();
                log.info("> NTF_LOUGOUT, ip: " + addr + " already offline");
                sessionService.removeSession(addr);
            }
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
    }

    private void startLoop() {
        while (!shutdown) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet); /* This would be blocking. */
                executorService.submit(() -> this.handlePacket(packet));
            } catch (IOException e) {
                // no-op.
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }
        }
    }

    public void start() throws IOException {
        socket = new DatagramSocket(port > 0 ? port : DEFAULT_LISTEN_PORT);
        executorService.submit(this::startLoop);
    }

    public void shutdown() {
        shutdown = true;
        try {
            Thread.sleep(100);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            executorService.shutdownNow();
            socket.close();
        }
    }
}
