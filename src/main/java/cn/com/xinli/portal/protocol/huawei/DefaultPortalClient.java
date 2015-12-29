package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Message;
import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.Packet;
import cn.com.xinli.portal.protocol.Protocol;
import cn.com.xinli.portal.protocol.UnsupportedProtocolException;
import cn.com.xinli.portal.protocol.support.AbstractPortalClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 * Default portal client.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class DefaultPortalClient extends AbstractPortalClient {
    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(DefaultPortalClient.class);

    /**
     * Associated protocol.
     */
    protected final AbstractHuaweiProtocol protocol;

    /**
     * Associated Nas.
     */
    private final Nas nas;

    public DefaultPortalClient(Nas nas, Protocol<?> protocol) {
        super(nas.getAuthType());
        this.nas = nas;
        if (AbstractHuaweiProtocol.class.isAssignableFrom(protocol.getClass())) {
            this.protocol = (AbstractHuaweiProtocol) protocol;
        } else {
            throw new UnsupportedProtocolException(protocol.getClass().getName());
        }
    }

    @Override
    protected HuaweiPacket createChapReqPacket(Credentials credentials) throws IOException {
        return Packets.newChapReq(protocol, credentials);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <p>
     * <code>CHAP password = MD5(CHAP id + Password + Challenge)</code>
     * </p>
     *
     * @param ack         challenge acknowledge packet.
     * @param credentials user credentials.
     * @return Huawei packet.
     * @throws IOException
     */
    @Override
    protected HuaweiPacket createChapAuthPacket(Packet ack, Credentials credentials) throws IOException {
        return Packets.newChapAuth(protocol, (HuaweiPacket) ack, credentials);
    }

    @Override
    protected HuaweiPacket createPapAuthPacket(Credentials credentials) throws IOException {
        return Packets.newPapAuth(protocol, credentials);
    }

    @Override
    protected HuaweiPacket createLogoutPacket(Credentials credentials) throws IOException {
        return Packets.newLogout(protocol, authType, credentials);
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param response response packet from remote.
     * @return packet.
     */
    private HuaweiPacket createAffAckPacket(HuaweiPacket response) {
        return Packets.newAffAck(protocol, response);
    }

    /**
     * Create request timeout packet.
     * <p>
     * NAK packet use {@link Enums.Type#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     * </p>
     *
     * @param request original request.
     * @return request timeout packet.
     */
    private HuaweiPacket createRequestTimeoutPacket(HuaweiPacket request) {
        return Packets.newTimeout(protocol, request);
    }

    @Override
    protected Optional<Packet> request(Packet request) throws IOException {
        DatagramSocket socket = null;
        HuaweiPacket req = (HuaweiPacket) request;

        try {
            /* Send request to remote. */
            socket = new DatagramSocket();
            socket.setSoTimeout(1000_000);
            ByteBuffer buffer = protocol.getCodecFactory().getEncoder()
                    .encode(req, nas.getSharedSecret());

            DatagramPacket out = new DatagramPacket(
                    buffer.array(), buffer.remaining(), nas.getInetAddress(), nas.getListenPort());
            socket.send(out);

            /* Try to receive from remote. */
            int capacity = 1024;
            byte[] buf = new byte[capacity];
            DatagramPacket response = new DatagramPacket(buf, buf.length);
            socket.receive(response);

            /* Decode response. */
            buffer.clear();
            buffer.put(response.getData(), 0, response.getLength());
            buffer.flip();
            HuaweiPacket responsePacket = protocol.getCodecFactory().getDecoder()
                    .decode(req.getAuthenticator(), buffer, nas.getSharedSecret());
            return Optional.ofNullable(responsePacket);
        } catch (SocketTimeoutException e) {
            logger.warn("* Receive from nas timeout, nas: ", nas);
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
    private void sendAffAck(HuaweiPacket ack) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        ByteBuffer buffer = protocol.getCodecFactory().getEncoder()
                .encode(ack, nas.getSharedSecret());

        DatagramPacket pack = new DatagramPacket(
                buffer.array(), buffer.remaining(), nas.getInetAddress(), nas.getListenPort());
        socket.send(pack);
        socket.close();
    }

    /**
     * Send a not-acknowledge packet to NAS (as request).
     *
     * @param nak not-acknowledge packet.
     * @throws IOException
     */
    private void sendTimeout(HuaweiPacket nak) throws IOException {
        sendAffAck(nak);
    }


    @Override
    protected Message<?> onChapRequestNotRespond(Packet request) throws IOException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        return Message.of(request, false, "NAS not respond to challenge request.");
    }

    @Override
    protected Message<?> onAuthenticationNotRespond(Packet request) throws IOException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        return Message.of(request, false, "NAS not respond to authentication request.");
    }

    @Override
    protected Message<?> onAuthenticationResponse(Packet response) throws IOException {
        sendAffAck(createAffAckPacket((HuaweiPacket) response));
        return PortalMessage.from((HuaweiPacket) response);
    }

    /**
     * {@inheritDoc}
     * <p>
     * If NAS respond with eror {@link Enums.LogoutError#GONE},
     * we treat it as success.
     *
     * @param response response.
     * @return
     * @throws IOException
     */
    @Override
    protected Message<?> onLogoutResponse(Packet response) throws IOException {
        HuaweiPacket packet = (HuaweiPacket) response;
        Optional<Enums.LogoutError> error = Enums.LogoutError.valueOf(packet.getError());
        error.orElseThrow(() -> new RuntimeException("Can't get error code from Huawei Portal response."));

        if (error.get() == Enums.LogoutError.GONE) {
            return Message.of(null, true, Enums.LogoutError.OK.getDescription());
        }
        return PortalMessage.from((HuaweiPacket) response);
    }

    @Override
    protected Message<?> onLogoutNotRespond(Packet request) throws IOException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        return Message.of(request, false, "NAS not respond to authentication request.");
    }
}
