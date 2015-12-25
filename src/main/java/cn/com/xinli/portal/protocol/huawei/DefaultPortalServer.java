package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.protocol.support.AbstractDatagramServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
    /** Log. */
    private static final Log log = LogFactory.getLog(DefaultPortalServer.class);

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
        log.info("> Portal Server started, listen on: " + this.portalServerPort + ".");
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
        protected void handlePacket(DatagramSocket socket, DatagramPacket packet) {
            try {
                if (HuaweiCodecFactory.verify(packet, sharedSecret)) {
                    HuaweiPacket in = (HuaweiPacket) codecFactory.getDecoder().decode(packet, sharedSecret);
                    byte[] ip = in.getIp();
                    //byte[] mac = in.getAttribute(Enums.Attribute.USER_MAC);
                    String address = InetAddress.getByAddress(ip).getHostAddress();
                    log.info("> NTF_LOGOUT, ip: " + address + " already offline");
                    sessionService.removeSession(address);
                }
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }
        }

        @Override
        protected boolean verifyPacket(DatagramPacket packet) throws IOException {
            return HuaweiCodecFactory.verify(packet, sharedSecret);
        }
    }

}
