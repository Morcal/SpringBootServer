package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.nio.CodecFactory;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.HuaweiCredentials;
import cn.com.xinli.portal.transport.*;
import cn.com.xinli.portal.transport.huawei.ClientHandler;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.RequestType;
import cn.com.xinli.portal.transport.huawei.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default portal client.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
final class DefaultPortalClient implements PortalClient {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DefaultPortalClient.class);

    /** Protocol codec factory. */
    private final CodecFactory<HuaweiPacket> codecFactory;

    /** Protocol endpoint. */
    private final Endpoint endpoint;

    /** Handler. */
    private final ClientHandler<HuaweiPacket> handler;

    /** Max serial number. */
    private static final int MAX_SERIAL = Short.MAX_VALUE * 2 + 1;

    /** Serial number random generator. */
    private static final Random random = new Random(System.currentTimeMillis());

    /** Serial number generator. */
    private static final AtomicInteger serial
            = new AtomicInteger((random.nextInt() & 0xFFFF) % MAX_SERIAL);

    /**
     * Generate a serial number.
     * <p>Serial numbers are unique in-a-time-range.</p>
     * @return serial number.
     */
    public static int nextSerialNum() {
        return serial.updateAndGet(i -> (i >= MAX_SERIAL + 1 ? 0 : i + 1));
    }

    public DefaultPortalClient(Endpoint endpoint,
                               CodecFactory<HuaweiPacket> codecFactory,
                               ClientHandler<HuaweiPacket> handler)
            throws TransportException {
        this.endpoint = endpoint;
        this.handler = handler;
        this.codecFactory = codecFactory;
    }

    /**
     * Create portal request without previous response.
     *
     * <p>Call this method when request does not need an associated
     * context which should exists in previous response, i.e.
     * CHAP authentication requests need Challenge response in previous
     * step (challenge).
     *
     * @param type request type.
     * @param credentials user credentials.
     * @param serialNum serial number.
     * @return request packet.
     * @throws IOException
     * @throws TransportException
     */
    HuaweiPacket createRequest(RequestType type, Credentials credentials, int serialNum)
            throws IOException, TransportException {
        return createRequest(type, credentials, null, serialNum);
    }

    /**
     * Create portal request with previous response as context.
     *
     * <p>Call this method when request need an associated
     * context which should exists in previous response, i.e.
     * CHAP authentication requests need Challenge response in previous
     * step (challenge).
     *
     * @param type request type.
     * @param credentials user credentials.
     * @param response previous response.
     * @param serialNum serial number.
     * @return request packet.
     * @throws IOException
     * @throws TransportException
     */
    HuaweiPacket createRequest(RequestType type,
                               Credentials credentials,
                               HuaweiPacket response,
                               int serialNum)
            throws IOException, TransportException {
        HuaweiCredentials cred = HuaweiCredentials.class.cast(credentials);
        Version version = endpoint.getVersion();
        switch (type) {
            case REQ_CHALLENGE:
                return Packets.newChapReq(version, cred, serialNum);

            case REQ_LOGOUT:
                return Packets.newLogout(version, endpoint.getAuthType(), cred, serialNum);

            case AFF_ACK_AUTH:
                return Packets.newAffAck(version, response);

            case NTF_LOGOUT:
                break;

            case REQ_AUTH:
                switch (endpoint.getAuthType()) {
                    case CHAP:
                        return Packets.newChapAuth(version, response, cred, serialNum);
                    case PAP:
                        return Packets.newPapAuth(version, cred, serialNum);
                }
            case REQ_INFO:
            case NTF_USERDISCOVERY:
            case NTF_USERIPCHANGE:
            case AFF_NTF_USERIPCHANGE:
            case WEB_STATUS_NOTIFY:
            default:
                break;
        }
        throw new UnsupportedTransportException("unsupported request: " + type.name());
    }

    /**
     * Create request timeout packet.
     *
     * <p>NAK packet use {@link RequestType#REQ_LOGOUT} as packet type.
     * NAK (TIMEOUT) packet error code must be 1. Serial number of a timeout
     * notify packet is same number as originate request.
     *
     * @param request original request.
     * @return request timeout packet.
     */
    private HuaweiPacket createRequestTimeoutPacket(HuaweiPacket request) {
        return Packets.newTimeout(endpoint.getVersion(), request);
    }

    /**
     * Send a request to NAS and receive response.
     *
     * @param request request packet.
     * @return response packet from NAS.
     * @throws IOException
     */
    protected Optional<HuaweiPacket> request(HuaweiPacket request) throws IOException {
        DatagramSocket socket = null;

        try {
            /* Send request to remote. */
            socket = new DatagramSocket();
            socket.setSoTimeout(10_000);
            ByteBuffer buffer = codecFactory.getEncoder().encode(request, endpoint.getSharedSecret());

            DatagramPacket out = new DatagramPacket(
                    buffer.array(), buffer.remaining(), endpoint.getAddress(), endpoint.getPort());
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
            HuaweiPacket responsePacket = codecFactory.getDecoder()
                    .decode(request.getAuthenticator(), buffer, endpoint.getSharedSecret());
            return Optional.ofNullable(responsePacket);
        } catch (SocketTimeoutException e) {
            logger.warn("* Receive from endpoint timeout, endpoint: {}", endpoint);
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
        ByteBuffer buffer = codecFactory.getEncoder()
                .encode(ack, endpoint.getSharedSecret());

        DatagramPacket pack = new DatagramPacket(
                buffer.array(), buffer.remaining(), endpoint.getAddress(), endpoint.getPort());
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

    /**
     * {@inheritDoc}
     *
     * <p>According to HUAWEI portal protocol, serial number should be unique
     * in a range of time. All serial numbers in a single authentication process.
     * and serial numbers differ from other authentication process.
     *
     * <p>SerialNo字段为报文的序列号，长度为 2 字节，由Portal Server随机生成，
     * Portal Server必须尽量保证不同认证流程的SerialNo在一定时间内不得重复，
     * 在同一个认证流程中所有报文的SerialNo相同。
     *
     * @param credentials user credentials.
     * @return result.
     * @throws IOException
     * @throws TransportException
     */
    @Override
    public Result login(Credentials credentials) throws IOException, TransportException {
        Objects.requireNonNull(credentials);

        Optional<HuaweiPacket> response;
        HuaweiPacket request;

        int serialNum = nextSerialNum(); /* Ensure same serial number in chap login. */
        switch (endpoint.getAuthType()) {
            case CHAP:
                HuaweiPacket challenge = createRequest(RequestType.REQ_CHALLENGE, credentials, serialNum);
                response = request(challenge);
                if (!response.isPresent()) {
                    /* Not respond, send timeout NAK, reqId = 0. */
                    sendTimeout(createRequestTimeoutPacket(challenge));
                    return handler.handleChapNotRespond(endpoint);
                }

                HuaweiPacket chapAck = response.get();
                /* Populate 'request id' issued by NAS/BRAS. */
                HuaweiCredentials.class.cast(credentials).setRequestId(chapAck.getReqId());

                Result chapResponse = handler.handleChapResponse(chapAck);
                if (logger.isTraceEnabled()) {
                    logger.trace("CHAP response: {}", chapResponse);
                }

                request = createRequest(RequestType.REQ_AUTH, credentials, chapAck, serialNum);
                response = request(request);
                break;

            case PAP:
                request = createRequest(RequestType.REQ_AUTH, credentials, serialNum);
                response = request(request);

                if (response.isPresent()) {
                    /* Populate 'request id' issued by NAS/BRAS. */
                    HuaweiCredentials.class.cast(credentials).setRequestId(response.get().getReqId());
                }
                break;

            default:
                throw new UnsupportedAuthenticationTypeException(endpoint.getAuthType());
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            logger.debug("Handle authentication response.");
            sendAffAck(createRequest(RequestType.AFF_ACK_AUTH, credentials, response.get(), serialNum));
            return handler.handleAuthenticationResponse(response.get());
        } else {
            logger.debug("Handle authentication timeout.");
            sendTimeout(createRequestTimeoutPacket(request));
            return handler.handleAuthenticationNotRespond(endpoint);
        }
    }

    @Override
    public Result logout(Credentials credentials) throws IOException, TransportException {
        Objects.requireNonNull(credentials);

        int serialNum = nextSerialNum();
        /* Create portal request to logout. */
        HuaweiPacket logout = createRequest(RequestType.REQ_LOGOUT, credentials, serialNum);
        Optional<HuaweiPacket> response = request(logout);

        if (!response.isPresent()) {
            logger.debug("Handle logout timeout.");
            sendTimeout(createRequestTimeoutPacket(logout));
            return handler.handleLogoutNotRespond(endpoint);
        } else {
            logger.debug("Handle logout response.");
            return handler.handleLogoutResponse(response.get());
        }
    }
}
