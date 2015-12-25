package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Message;
import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.Packet;
import cn.com.xinli.portal.protocol.Protocol;
import cn.com.xinli.portal.protocol.support.AbstractPortalClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.Optional;

/**
 * Default portal client.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class DefaultPortalClient extends AbstractPortalClient {
    /** Log. */
    private static final Log log = LogFactory.getLog(DefaultPortalClient.class);

    /** Associated protocol. */
    protected final Protocol protocol;

    public DefaultPortalClient(Nas nas, Protocol protocol) {
        super(nas);
        this.protocol = protocol;
    }

    @Override
    protected Packet createChapReqPacket(Credentials credentials) {
        return Utils.createChapReqPacket(protocol, credentials);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * <code>CHAP password = MD5(CHAP id + Password + Challenge)</code>
     * </p>
     * @param ack   challenge acknowledge packet.
     * @param credentials user credentials.
     * @return
     * @throws IOException
     */
    @Override
    protected Packet createChapAuthPacket(Packet ack, Credentials credentials) throws IOException {
        return Utils.createChapAuthPacket(protocol, ack, credentials);
    }

    /**
     * Create PAP authentication packet.
     *
     * @param credentials user credentials.
     * @return PAP authentication packet.
     */
    @Override
    protected Packet createPapAuthPacket(Credentials credentials) throws IOException {
        return Utils.createPapAuthPacket(protocol, credentials);
    }

    /**
     * Create logout request packet.
     *
     * @param authType    authentication type.
     * @param credentials user credentials.
     * @return logout request packet,
     * or null if ip address in credentials is unknown.
     */
    @Override
    protected Packet createLogoutPacket(AuthType authType, Credentials credentials) throws IOException {
        return Utils.createLogoutPacket(protocol, authType, credentials);
    }

    /**
     * Create affirmative acknowledge packet.
     *
     * @param response response packet from remote.
     * @return packet.
     */
    private Packet createAffAckPacket(Packet response) {
        return Utils.createAffAckPacket(protocol, response);
    }

    /**
     * Create request timeout packet.
     * <p>
     * NAK packet use {@link Enums.Type#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1.
     * </p>
     * @param request original request.
     * @return request timeout packet.
     */
    private Packet createRequestTimeoutPacket(Packet request) {
        return Utils.createRequestTimeoutPacket(protocol, request);
    }

    @Override
    protected Optional<Packet> request(Packet packet) throws IOException {
        DatagramSocket socket = null;
        HuaweiPacket hwPkt = (HuaweiPacket) packet;

        try {
            /* Send request to remote. */
            socket = new DatagramSocket();
            socket.setSoTimeout(10_000);
            DatagramPacket request = protocol.getCodecFactory().getEncoder()
                    .encode(packet, nas.getInetAddress(), nas.getListenPort(), nas.getSharedSecret());
            socket.send(request);

            /* Try to receive from remote. */
            int capacity = 1024;
            byte[] buf = new byte[capacity];
            DatagramPacket response = new DatagramPacket(buf, buf.length);
            socket.receive(response);

            /* Decode response. */
            Packet responsePacket = protocol.getCodecFactory().getDecoder()
                    .decode(hwPkt.getAuthenticator(), response, nas.getSharedSecret());
            return Optional.ofNullable(responsePacket);
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
    private void sendAffAck(Packet ack) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket pack = protocol.getCodecFactory().getEncoder()
                .encode(ack, nas.getInetAddress(), nas.getListenPort(), nas.getSharedSecret());
        socket.send(pack);
        socket.close();
    }

    /**
     * Send a not-acknowledge packet to NAS (as request).
     * @param nak not-acknowledge packet.
     * @throws IOException
     */
    private void sendTimeout(Packet nak) throws IOException {
        sendAffAck(nak);
    }


    @Override
    protected Message<Packet> handleChapRequestNotRespond(Packet request) throws IOException {
        sendTimeout(createRequestTimeoutPacket(request));
        return Message.of(request, false, "NAS not respond to challenge request.");
    }

    @Override
    protected Message<Packet> handleAuthenticationNotRespond(Packet request) throws IOException {
        HuaweiPacket packet = (HuaweiPacket) request;
        sendTimeout(createRequestTimeoutPacket(packet));
        return Message.of(request, false, "NAS not respond to authentication request.");
    }

    @Override
    protected Message<Packet> handleAuthenticationResponse(Packet response) throws IOException {
        HuaweiPacket packet = (HuaweiPacket) response;
        sendAffAck(createAffAckPacket(packet));
        return PortalMessage.from(packet);
    }

    @Override
    protected Message<Packet> handleLogoutResponse(Packet response) throws IOException {
        if (!response.isSuccess()) {
            HuaweiPacket packet = (HuaweiPacket) response;
            if (packet.getError() == Enums.LogoutError.GONE.code()) {
                return Message.of(null, true, Enums.LogoutError.OK.getDescription());
            }
        }
        return PortalMessage.from((HuaweiPacket) response);
    }

    @Override
    protected Message<Packet> handleLogoutNotRespond(Packet request) throws IOException {
        HuaweiPacket packet = (HuaweiPacket) request;
        sendTimeout(createRequestTimeoutPacket(packet));
        return Message.of(request, false, "NAS not respond to authentication request.");
    }
}
