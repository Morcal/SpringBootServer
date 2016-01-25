package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.PortalServerConfig;
import cn.com.xinli.portal.transport.support.AbstractDatagramServer;
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
 * handles incoming portal request from NAS/BRAS.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
final class HuaweiPortalServer implements PortalServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPortalServer.class);

    /** Default listen port. */
    private static final int DEFAULT_LISTEN_PORT = 2000;

    /** Portal server codec factory. */
    private final HuaweiCodecFactory codecFactory;

    /** Portal server shared secret. */
    private final String sharedSecret;

    /** Internal datagram server. */
    private final DatagramPortalServer datagramPortalServer;

    private final int portalServerPort;

    private final PortalServerHandler handler;

    public HuaweiPortalServer(PortalServerConfig portalServerConfig, PortalServerHandler handler) {
        this.handler = handler;
        this.codecFactory = new HuaweiCodecFactory();
        this.sharedSecret = portalServerConfig.getSharedSecret();
        this.portalServerPort = portalServerConfig.getListenPort() > 0 ?
                portalServerConfig.getListenPort() : DEFAULT_LISTEN_PORT;
        this.datagramPortalServer
                = new DatagramPortalServer(portalServerPort, portalServerConfig.getThreadSize());
    }

    @Override
    public void start() throws IOException {
        this.datagramPortalServer.start();
        logger.info("Portal Server started, listen on: {}.", this.portalServerPort);
    }

    @Override
    public void shutdown() {
        this.datagramPortalServer.shutdown();
    }

    /** Internal datagram server. */
    class DatagramPortalServer extends AbstractDatagramServer {
        DatagramPortalServer(int port, int threadSize) {
            super(port, threadSize, "portal-server");
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                if (HuaweiCodecFactory.verify(buffer, sharedSecret)) {
                    HuaweiPacket in = codecFactory.getDecoder().decode(buffer, sharedSecret);
                    byte[] ip = in.getIp();
                    //byte[] mac = in.getAttribute(Enums.AttributeType.USER_MAC);
                    String address = InetAddress.getByAddress(ip).getHostAddress();
                    logger.info("NTF_LOGOUT, ip: {} already offline", ip);
                    LogoutError error = handler.handleNtfLogout(address);
                    HuaweiPacket ack = Packets.newNtfLogoutAck(in, error);
                    ByteBuffer buf = codecFactory.getEncoder().encode(in.getAuthenticator(), ack, sharedSecret);
                    channel.send(buf, remote);
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
