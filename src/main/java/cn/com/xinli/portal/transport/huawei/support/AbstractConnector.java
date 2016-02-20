package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.nio.CodecFactory;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.Connector;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.UnsupportedAuthenticationTypeException;
import cn.com.xinli.portal.transport.UnsupportedTransportException;
import cn.com.xinli.portal.transport.huawei.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract portal connector.
 *
 * <p>This class provides a stateless abstract portal connector to
 * support HUAWEI portal protocol.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public abstract class AbstractConnector implements Connector<ExtendedInformation> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractConnector.class);

    /** Protocol codec factory. */
    protected final CodecFactory<Packet> codecFactory;

    /** Protocol endpoint. */
    protected final Endpoint endpoint;

    /** Handler. */
    protected final ConnectorHandler handler;

    /** Max serial number. */
    private static final int MAX_SERIAL = Short.MAX_VALUE * 2 + 1;

    /** Serial number random generator. */
    private static final Random random = new Random(System.currentTimeMillis());

    /** Serial number generator. */
    private static final AtomicInteger serial
            = new AtomicInteger((random.nextInt() & 0xFFFF) % MAX_SERIAL);

    /**
     * Generate a serial number.
     *
     * <p>Serial numbers are unique in-a-time-range.
     *
     * @return serial number.
     */
    public static int nextSerialNum() {
        return serial.updateAndGet(i -> (i >= MAX_SERIAL + 1 ? 0 : i + 1));
    }

    public AbstractConnector(Endpoint endpoint,
                             CodecFactory<Packet> codecFactory,
                             ConnectorHandler handler)
            throws TransportException {
        this.endpoint = endpoint;
        this.handler = handler;
        this.codecFactory = codecFactory;
    }

    /**
     * Send a request to remote NAS and receive response.
     *
     * @param request request packet.
     * @return response packet from NAS.
     * @throws IOException
     */
    public abstract Optional<Packet> request(Packet request) throws IOException;

    /**
     * Send a packet to remote NAS (as request) and doesn't
     * receive response (even if response presents).
     *
     * <p>Call this method instead of {@link #request(Packet)} only if
     * remote will not respond.
     *
     * @param ack acknowledge packet.
     * @throws IOException
     */
    protected abstract void send(Packet ack) throws IOException;

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
     * @param extendedInformation extended Information.
     * @param serialNum serial number.
     * @return request packet.
     * @throws IOException
     * @throws TransportException
     */
    public Packet createRequest(RequestType type,
                                 Credentials credentials,
                                 ExtendedInformation extendedInformation,
                                 int serialNum) throws IOException, TransportException {
        return createRequest(type, credentials, null, extendedInformation, serialNum);
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
     * @param extendedInformation extended Information.
     * @param serialNum serial number.
     * @return request packet.
     * @throws IOException
     * @throws TransportException
     */
    public Packet createRequest(RequestType type,
                                 Credentials credentials,
                                 Packet response,
                                 ExtendedInformation extendedInformation,
                                 int serialNum) throws IOException, TransportException {
        Version version = endpoint.getVersion();
        switch (type) {
            case REQ_CHALLENGE:
                return Packets.newChapReq(version, credentials, serialNum);

            case REQ_LOGOUT:
                return Packets.newLogout(version, endpoint.getAuthType(), credentials, extendedInformation, serialNum);

            case AFF_ACK_AUTH:
                return Packets.newAffAck(version, response);

            case NTF_LOGOUT:
                break;

            case REQ_AUTH:
                switch (endpoint.getAuthType()) {
                    case CHAP:
                        return Packets.newChapAuth(version, response, credentials, serialNum);
                    case PAP:
                        return Packets.newPapAuth(version, credentials, serialNum);
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
    private Packet createRequestTimeoutPacket(Packet request) {
        return Packets.newTimeout(endpoint.getVersion(), request);
    }

    /**
     * Send an acknowledge packet to NAS (as request).
     *
     * @param ack acknowledge packet.
     * @throws IOException
     */
    private void sendAffAck(Packet ack) throws IOException {
        send(ack);
    }

    /**
     * Send a not-acknowledge packet to NAS (as request).
     *
     * @param nak not-acknowledge packet.
     * @throws IOException
     */
    private void sendTimeout(Packet nak) throws IOException {
        sendAffAck(nak);
    }

    /**
     * {@inheritDoc}
     *
     * <p>According to HUAWEI portal protocol, serial number should be unique
     * in a range of time. Serial numbers in a single authentication process must
     * be the same and that serial number differs from serial numbers
     * within other authentication process.
     *
     * <p>SerialNo字段为报文的序列号，长度为 2 字节，由Portal Server随机生成，
     * Portal Server必须尽量保证不同认证流程的SerialNo在一定时间内不得重复，
     * 在同一个认证流程中所有报文的SerialNo相同。
     *
     * @param credentials user credentials.
     * @return Extended Information.
     * @throws IOException
     * @throws TransportException
     */
    @Override
    public ExtendedInformation login(Credentials credentials) throws IOException, TransportException {
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);

        Optional<Packet> response;
        Packet request;

        int serialNum = nextSerialNum(); /* Ensure same serial number in login. */
        ExtendedInformation extendedInformation = new ExtendedInformation();

        switch (endpoint.getAuthType()) {
            case CHAP:
                Packet challenge = createRequest(RequestType.REQ_CHALLENGE, credentials, null, serialNum);
                response = request(challenge);
                if (!response.isPresent()) {
                    /* Not respond, send timeout NAK, reqId = 0. */
                    sendTimeout(createRequestTimeoutPacket(challenge));
                    handler.handleServerNotRespond(endpoint);
                    return null;
                }

                Packet chapAck = response.get();
                /* Populate 'request id' issued by NAS/BRAS. */
                extendedInformation.setRequestId(chapAck.getReqId());

                handler.handleChapResponse(chapAck);
                request = createRequest(RequestType.REQ_AUTH, credentials, chapAck, null, serialNum);
                response = request(request);
                break;

            case PAP:
                request = createRequest(RequestType.REQ_AUTH, credentials, null, serialNum);
                response = request(request);

                if (response.isPresent()) {
                    /* Populate 'request id' issued by NAS/BRAS. */
                    extendedInformation.setRequestId(response.get().getReqId());
                }
                break;

            default:
                throw new UnsupportedAuthenticationTypeException(endpoint.getAuthType());
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            logger.debug("Handle authentication response.");
            sendAffAck(createRequest(RequestType.AFF_ACK_AUTH, credentials, response.get(), null, serialNum));
            handler.handleAuthenticationResponse(response.get());
        } else {
            logger.debug("Handle authentication timeout.");
            sendTimeout(createRequestTimeoutPacket(request));
            handler.handleServerNotRespond(endpoint);
        }

        return extendedInformation;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Send {@link RequestType#REQ_LOGOUT} to logout.
     * If remote not respond, send request-timeout.
     *
     * <p>由Portal Server向BAS设备发出的Type为REQ_LOGOUT的报文其SerialNo值
     * 分两种情况：当ErrorCode为0时，SerialNo值为一个随机生成数；
     * 当ErrorCode为1时，SerialNo值可能和Type值为1或3的报文相同，
     * 具体要看是请求Challenge超时还是请求认证超时；
     *
     * @param credentials user credentials.
     * @throws IOException
     * @throws TransportException
     */
    @Override
    public void logout(Credentials credentials, ExtendedInformation extendedInformation)
            throws IOException, TransportException {
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);
        Objects.requireNonNull(extendedInformation, "extended information can not be empty.");

        int serialNum = nextSerialNum();
        /* Create portal request to logout. */
        Packet logout = createRequest(RequestType.REQ_LOGOUT, credentials, extendedInformation, serialNum);
        Optional<Packet> response = request(logout);

        if (!response.isPresent()) {
            logger.debug("Handle logout timeout.");
            sendTimeout(createRequestTimeoutPacket(logout));
            handler.handleServerNotRespond(endpoint);
        } else {
            logger.debug("Handle logout response.");
            handler.handleLogoutResponse(response.get());
        }
    }
}
