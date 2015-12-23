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

    /** Huawei Protocol version. */
    protected final int version;

    public AbstractPortalClient(int version, Nas nas, CodecFactory codecFactory) {
        this.nas = nas;
        this.version = version;
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
            socket.setSoTimeout(10_000);
            DatagramPacket request = codecFactory.getEncoder()
                    .encode(packet, Nas.getInetAddress(nas), nas.getListenPort(), nas.getSharedSecret());
            socket.send(request);

            DatagramPacket response = createResponseDatagramPacket();
            socket.receive(response);

            Packet responsePacket = codecFactory.getDecoder()
                    .decode(packet.getAuthenticator(), response, nas.getSharedSecret());
            if (responsePacket == null) {
                return Optional.empty();
            } else {
                return Optional.of(responsePacket);
            }
        } catch (SocketTimeoutException e) {
            log.warn("* Receive from nas timeout, nas: " + nas);
            return Optional.empty();
        } finally {
            if (socket != null) {
                socket.close();
                socket.disconnect();
            }
        }
    }

    /**
     * Send an acknowledge packet to NAS (as request).
     *
     * @param ack acknowledge packet.
     * @throws IOException
     */
    public void ack(Packet ack) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket pack = codecFactory.getEncoder()
                .encode(ack, Nas.getInetAddress(nas), nas.getListenPort(), nas.getSharedSecret());
        socket.send(pack);
        socket.close();
    }

    /**
     * Send a not-acknowledge packet to NAS (as request).
     * @param nak not-acknowledge packet.
     * @throws IOException
     */
    public void nak(Packet nak) throws IOException {
        ack(nak);
    }
}
