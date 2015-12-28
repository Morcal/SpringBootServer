package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.protocol.support.AbstractDatagramServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * Default portal server.
 *
 * <p>Portal server listens on udp port at {@link #portalServerPort} and
 * handles incoming portal request from NAS/BRAS. </p>
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
public class DefaultPortalServer  {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DefaultPortalServer.class);

    /** Default listen port. */
    private static final int DEFAULT_LISTEN_PORT = 2000;

    /** Portal server codec factory. */
    private final HuaweiCodecFactory codecFactory;

    /** Session service. */
    private final SessionService sessionService;

    /** Portal server shared secret. */
    private final String sharedSecret;

    /** Internal datagram server. */
    private final DatagramPortalServer datagramPortalServer;

    private final int portalServerPort;

    public DefaultPortalServer(ServerConfig serverConfig, SessionService sessionService) {
        this.sessionService = sessionService;
        this.codecFactory = new HuaweiCodecFactory();
        this.sharedSecret = serverConfig.getPortalServerSharedSecret();
        this.portalServerPort = serverConfig.getPortalServerListenPort() > 0 ?
                serverConfig.getPortalServerListenPort() : DEFAULT_LISTEN_PORT;
        this.datagramPortalServer
                = new DatagramPortalServer(portalServerPort, serverConfig.getPortalServerThreadSize());
    }

    public void start() throws IOException {
        this.datagramPortalServer.start();
        logger.info("> Portal Server started, listen on: " + this.portalServerPort + ".");
    }

    public void shutdown() {
        this.datagramPortalServer.shutdown();
    }

    /** Internal datagram server. */
    class DatagramPortalServer extends AbstractDatagramServer {
        DatagramPortalServer(int port, int threadSize) {
            super(port, threadSize);
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                if (HuaweiCodecFactory.verify(buffer, sharedSecret)) {
                    HuaweiPacket in = codecFactory.getDecoder().decode(buffer, sharedSecret);
                    byte[] ip = in.getIp();
                    //byte[] mac = in.getAttribute(Enums.Attribute.USER_MAC);
                    String address = InetAddress.getByAddress(ip).getHostAddress();
                    logger.info("> NTF_LOGOUT, ip: " + address + " already offline");
                    sessionService.removeSession(address);
                }
            } catch (Exception e) {
                logger.error("Portal server handle packet error", e);
            }
        }

        @Override
        protected ByteBuffer createReceiveBuffer() {
            return ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
        }

        @Override
        protected boolean verifyPacket(ByteBuffer buffer) throws IOException {
            return HuaweiCodecFactory.verify(buffer, sharedSecret);
        }
    }

}
