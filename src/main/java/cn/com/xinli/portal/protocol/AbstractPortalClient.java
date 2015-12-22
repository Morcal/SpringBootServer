package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.Nas;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Optional;

/**
 * Abstract UDP based portal client.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public abstract class AbstractPortalClient implements PortalClient {
    /** Log. */
    private static final Log log = LogFactory.getLog(AbstractPortalClient.class);

    /** Associated NAS. */
    protected final Nas nas;

    /** Protocol. */
    protected final CodecFactory codecFactory;

    public AbstractPortalClient(Nas nas, CodecFactory codecFactory) {
        this.nas = nas;
        this.codecFactory = codecFactory;
    }

    /**
     * Create an empty datagram packet for receiving.
     * @return empty {@link DatagramPacket} for receiving.
     */
    protected abstract DatagramPacket createResponseDatagramPacket();

    /**
     * Send a request to NAS and receive response.
     *
     * @param packet request packet.
     * @return response packet from NAS.
     * @throws IOException
     */
    protected Optional<Packet> request(Packet packet) throws IOException {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(3000);
            DatagramPacket pack = codecFactory.getEncoder().encode(packet, nas.getSharedSecret());
            socket.send(pack);

            DatagramPacket response = createResponseDatagramPacket();
            socket.receive(response);

            Packet responsePacket = codecFactory.getDecoder().decode(response, nas.getSharedSecret());
            if (responsePacket == null) {
                return Optional.empty();
            } else {
                return Optional.of(responsePacket);
            }
        } catch (SocketTimeoutException e) {
            log.warn("* Receive from nas: " + nas + " timeout.");
            return Optional.empty();
        } finally {
            if (socket != null) {
                socket.close();
                socket.disconnect();
            }
        }
    }

    /**
     * Send an acknowledge packet to NAS.
     *
     * @param ack acknowledge packet.
     * @throws IOException
     */
    public void ack(Packet ack) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket pack = codecFactory.getEncoder().encode(ack, nas.getSharedSecret());
        socket.send(pack);
        socket.close();
    }

    /**
     * Send a not-acknowledge packet to NAS.
     * @param nak not-acknowledge packet.
     * @throws IOException
     */
    public void nak(Packet nak) throws IOException {
        ack(nak);
    }
}
