package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.Credentials;
import cn.com.xinli.portal.core.Nas;
import cn.com.xinli.portal.transport.*;
import cn.com.xinli.portal.transport.support.AbstractPortalClient;
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
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
final class DefaultPortalClient extends AbstractPortalClient {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DefaultPortalClient.class);

    /** Associated protocol. */
    protected final AbstractHuaweiProtocol protocol;

    /** Associated Nas. */
    private final Nas nas;

    public DefaultPortalClient(Nas nas, Protocol<?> protocol) throws PortalProtocolException {
        super(nas.getAuthType());
        this.nas = nas;
        if (AbstractHuaweiProtocol.class.isAssignableFrom(protocol.getClass())) {
            this.protocol = (AbstractHuaweiProtocol) protocol;
        } else {
            throw new UnsupportedPortalProtocolException(protocol.getClass().getName());
        }
    }

    @Override
    protected HuaweiPacket createChapReqPacket(Credentials credentials) throws IOException {
        return Packets.newChapReq(protocol, credentials);
    }

    /**
     * {@inheritDoc}
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
     * NAK packet use {@link RequestType#REQ_LOGOUT} as packet type.
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
            socket.setSoTimeout(10_000);
            ByteBuffer buffer = protocol.getCodecFactory().getEncoder()
                    .encode(req, nas.getSharedSecret());

            DatagramPacket out = new DatagramPacket(
                    buffer.array(), buffer.remaining(), nas.getNetworkAddress(), nas.getListenPort());
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
                buffer.array(), buffer.remaining(), nas.getNetworkAddress(), nas.getListenPort());
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
    protected Result onChapRespond(Packet response) throws IOException, PortalProtocolException {
        HuaweiPacket packet = (HuaweiPacket) response;
        Optional<ChallengeError> err = ChallengeError.valueOf(packet.getError());

        err.orElseThrow(() ->
                new UnrecognizedResponseException(
                        "CHAP response error code: " + packet.getError()));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(packet);

            case REJECTED:
                error = ProtocolError.CHALLENGE_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = ProtocolError.CHALLENGE_ALREADY_ONLINE;
                break;

            case WAIT:
                error = ProtocolError.CHALLENGE_UNAVAILABLE;
                break;

            case FAILED:
                error = ProtocolError.CHALLENGE_FAILURE;
                break;
        }

         throw new ChallengeException(error, error.getReason());
    }

    @Override
    protected Result onChapRequestNotRespond(Packet request) throws IOException, PortalProtocolException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        throw new NasNotRespondException(nas.toString());
    }

    @Override
    protected Result onAuthenticationNotRespond(Packet request) throws IOException, PortalProtocolException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        throw new NasNotRespondException(nas.toString());
    }

    @Override
    protected Result onAuthenticationResponse(Packet response)
            throws IOException, PortalProtocolException {
        HuaweiPacket packet = (HuaweiPacket) response;
        sendAffAck(createAffAckPacket(packet));

        Optional<AuthError> err = AuthError.valueOf(packet.getError());
        err.orElseThrow(() ->
            new UnrecognizedResponseException("authentication error code:" + packet.getError()));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(packet);

            case REJECTED:
                error = ProtocolError.AUTHENTICATION_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = ProtocolError.AUTHENTICATION_ALREADY_ONLINE;
                break;

            case WAIT:
                error = ProtocolError.AUTHENTICATION_UNAVAILABLE;
                break;

            case FAILED:
                error = ProtocolError.AUTHENTICATION_FAILURE;
                break;
        }

        throw new AuthenticationException(error, Packets.buildText(packet));
    }

    /**
     * {@inheritDoc}
     * <p>
     * If NAS respond with error {@link LogoutError#GONE},
     * we treat it as success.
     *
     * @param response response.
     * @return message.
     * @throws IOException
     */
    @Override
    protected Result onLogoutResponse(Packet response) throws IOException, PortalProtocolException {
        HuaweiPacket packet = (HuaweiPacket) response;
        Optional<LogoutError> err = LogoutError.valueOf(packet.getError());
        err.orElseThrow(() ->
                new UnrecognizedResponseException("logout error: " + packet.getError() + "."));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(packet);

            case REJECTED:
                error = ProtocolError.LOGOUT_REJECTED;
                break;

            case FAILED:
                error = ProtocolError.LOGOUT_FAILURE;
                break;

            case GONE:
                error = ProtocolError.LOGOUT_ALREADY_GONE;
                break;
        }
        throw new LogoutException(error, error.getReason());
    }

    @Override
    protected Result onLogoutNotRespond(Packet request) throws IOException, PortalProtocolException {
        sendTimeout(createRequestTimeoutPacket((HuaweiPacket) request));
        throw new NasNotRespondException(nas.toString());
    }
}
