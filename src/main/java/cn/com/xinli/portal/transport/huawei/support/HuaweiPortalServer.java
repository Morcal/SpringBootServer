package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.nio.support.AbstractDatagramServer;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.TransportUtils;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.UnsupportedAuthenticationTypeException;
import cn.com.xinli.portal.transport.huawei.*;
import cn.com.xinli.portal.transport.huawei.nio.ByteBufferCodecFactory;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default portal server.
 *
 * <p>Portal server listens on local port, receives incoming requests encoded
 * in portal protocol, and serves those requests and responds back to remote
 * client.
 *
 * <p>Portal server listens on udp port at {@link #endpoint} and
 * handles incoming portal request from NAS/BRAS.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
final class HuaweiPortalServer implements PortalServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPortalServer.class);

    /** Default worker thread size. */
    private static final int DEFAULT_THREAD_SIZE = 4;

    /** Portal server codec factory. */
    private final ByteBufferCodecFactory codecFactory;

    /** Internal datagram server. */
    private final DatagramPortalServer datagramPortalServer;

    /** Server handler. */
    private final ServerHandler handler;

    /** HUAWEI portal protocol endpoint. */
    private Endpoint endpoint;

    /**
     * Server request id generator.
     * <p>Request id(s) are generated by portal server.
     */
    private final AtomicInteger reqId = new AtomicInteger(0);

    private final Map<Integer, String> taken = new ConcurrentHashMap<>();

    HuaweiPortalServer(Endpoint endpoint, ServerHandler handler) {
        this(endpoint, handler, DEFAULT_THREAD_SIZE);
    }

    HuaweiPortalServer(Endpoint endpoint, ServerHandler handler, int threadSize) {
        this.handler = handler;
        this.codecFactory = new ByteBufferCodecFactory();
        this.endpoint = endpoint;
        this.datagramPortalServer
                = new DatagramPortalServer(endpoint.getPort(), threadSize);
    }

    /**
     * Get next request id.
     * @return request id.
     */
    private int nextReqId(String ip) {
        Objects.requireNonNull(ip, "Huawei portal server request id, ip is empty.");
        int next;
        do {
            next = reqId.updateAndGet(i -> (i >= Short.MAX_VALUE - 1 ? 0 : i + 1));
        } while (taken.putIfAbsent(next, ip) != null);

        return next;
    }

    public void release(int requestId) {
        taken.remove(requestId);
    }

    @Override
    public void start() throws IOException {
        this.datagramPortalServer.start();
        logger.info("Portal Server started, endpoint: {}.", this.endpoint);
    }

    @Override
    public void shutdown() {
        this.datagramPortalServer.shutdown();
    }

    /**
     * Convert ip address in a byte array to a hex string.
     * @param ip ip address.
     * @return hex string.
     */
    private static String ipHexString(byte[] ip) {
        return Hex.encodeHexString(ip);
    }

    /**
     * Handle incoming challenge request.
     * @param channel datagram channel.
     * @param request request.
     * @param remote remote address.
     * @throws IOException
     */
    private void handleChallenge(DatagramChannel channel,
                                 Packet request,
                                 SocketAddress remote) throws IOException {
        logger.info("REQ_CHALLENGE, request: {}, remote: {}", request, remote);

        String ip = ipHexString(request.getIp());
        int req = nextReqId(ip);
        List<String> results = new ArrayList<>();
        ChallengeError error = handler.challenge(ip, req, results);

        Packet ack;
        if (error ==  ChallengeError.OK) {
            String challenge = results.get(0);
            ack = Packets.newChallengeAck(
                    endpoint.getAddress(), challenge.getBytes(), req, error, request);
            if (logger.isDebugEnabled()) {
                logger.debug("[NAS] challenge created: {}.", challenge);
            }
        } else {
            ack = Packets.newChallengeAck(
                    endpoint.getAddress(), "challenge".getBytes(), req, error, request);
        }

        channel.send(codecFactory.getEncoder()
                        .encode(request.getAuthenticator(), ack, endpoint.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    /**
     * Handle authentication with CHAP and PAP.
     * @param requestId request id.
     * @param username username.
     * @param ip user ip address.
     * @param authType authentication type.
     * @param attributes packet attributes.
     * @return authentication error.
     * @throws UnsupportedAuthenticationTypeException
     * @throws IOException
     */
    protected AuthError handleChapAndPap(final int requestId,
                                       final String username,
                                       final String ip,
                                       AuthType authType,
                                       Collection<Packet.Attribute> attributes)
            throws UnsupportedAuthenticationTypeException, IOException {
        final byte[] password;
        switch (authType) {
            case CHAP:
                Optional<Packet.Attribute> chapPassword = attributes.stream()
                        .filter(attr -> attr.getType() == AttributeType.CHALLENGE_PASSWORD.code())
                        .findFirst();
                if (!chapPassword.isPresent()) {
                    return AuthError.FAILED;
                }

                password = chapPassword.get().getValue();
                break;

            case PAP:
                Optional<Packet.Attribute> pwd = attributes.stream()
                        .filter(attr -> attr.getType() == AttributeType.PASSWORD.code())
                        .findFirst();
                if (pwd.isPresent()) {
                    return AuthError.FAILED;
                }
                password = pwd.get().getValue();
                break;

            default:
                throw new UnsupportedAuthenticationTypeException(authType);
        }

        //FIXME collect mac address from request.
        return handler.authenticate(
                requestId,
                Credentials.of(username, Hex.encodeHexString(password), ip, ""),
                authType);
    }

    /**
     * Handle incoming authentication request.
     * @param channel datagram channel.
     * @param request incoming request.
     * @param remote remote address.
     * @throws IOException
     */
    private void handleAuth(DatagramChannel channel,
                            Packet request,
                            SocketAddress remote) throws IOException {
        logger.info("REQ_AUTH, request: {}, remote: {}", request, remote);

        Collection<Packet.Attribute> attributes = request.getAttributes();
        Optional<Packet.Attribute> username = attributes.stream()
                .filter(attr -> attr.getType() == AttributeType.USER_NAME.code())
                .findFirst();

        final int requestId = request.getReqId();
        AuthError error;
        do {
            if (!username.isPresent()) {
                error = AuthError.FAILED;
                break;
            }

            final String user = new String(username.get().getValue());
            final String ip = ipHexString(request.getIp());
            AuthType authType = AuthType.valueOf(request.getAuthType());

            try {
                error = handleChapAndPap(requestId, user, ip, authType, attributes);
            } catch (UnsupportedAuthenticationTypeException e) {
                error = AuthError.FAILED;
            }

        } while (false);

        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newAuthAck(endpoint.getAddress(), requestId, error, request),
                        endpoint.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    /**
     * Handle incoming logout request.
     * @param channel datagram channel.
     * @param request incoming request.
     * @param remote remote address.
     * @throws IOException
     */
    private void handleLogout(DatagramChannel channel,
                              Packet request,
                              SocketAddress remote) throws IOException {
        logger.info("REQ_LOGOUT, request: {}, remote: {}", request, remote);

        final String ip = ipHexString(request.getIp());
        final int requestId = request.getReqId();

        LogoutError error;
        if (!taken.containsKey(requestId)) {
            error = LogoutError.GONE;
        } else {
            Credentials credentials = Credentials.of("", "", ip, "");
            error = handler.logout(credentials);
            if (error == LogoutError.OK) {
                taken.remove(requestId);
            }
        }

        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newLogoutAck(endpoint.getAddress(), error, request),
                        endpoint.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    private void handleNtfLogout(DatagramChannel channel,
                                 Packet request,
                                 SocketAddress remote) throws IOException {
        logger.info("NTF_LOGOUT, request: {}, remote: {}", request, remote);
        byte[] ip = request.getIp();
        //byte[] mac = in.getAttribute(Enums.AttributeType.USER_MAC);
        String userIp = InetAddress.getByAddress(ip).getHostAddress();

        byte[] text = request.getAttribute(AttributeType.TEXT_INFO);
        if (text.length > 0 && logger.isTraceEnabled()) {
            logger.trace("NTF_LOGOUT TEXT: {}", new String(text));
        }

        // BAS_IP is mandatory.
        byte[] nas = request.getAttribute(AttributeType.BAS_IP);
        if (nas.length > 0) {
            String nasIp = TransportUtils.getIp4Address(nas);
            LogoutError error = handler.ntfLogout(nasIp, userIp);
            Packet ack = Packets.newNtfLogoutAck(InetAddress.getByAddress(nas), request, error);
            ByteBuffer buf = codecFactory.getEncoder()
                    .encode(request.getAuthenticator(), ack, endpoint.getSharedSecret());
            channel.send(buf, remote);
        }
    }

    /** Internal datagram server. */
    class DatagramPortalServer extends AbstractDatagramServer {
        DatagramPortalServer(int port, int threadSize) {
            super(port, threadSize, "portal-server");
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                Packet in = codecFactory.getDecoder()
                        .decode(buffer, endpoint.getSharedSecret());
                Optional<RequestType> type = RequestType.valueOf(in.getType());
                if (type.isPresent()) {
                    switch (type.get()) {
                        case REQ_CHALLENGE:
                            handleChallenge(channel, in, remote);
                            break;

                        case REQ_AUTH:
                            handleAuth(channel , in, remote);
                            break;

                        case REQ_LOGOUT:
                            handleLogout(channel, in, remote);
                            break;

                        case NTF_LOGOUT:
                            handleNtfLogout(channel, in, remote);
                            break;

                        case AFF_ACK_AUTH:
                            logger.info("AFF_ACK_AUTH affirmative acknowledged received.");
                            break;

                        default:
                            logger.warn("[NAS] Unsupported operation type: {}.", type.get().name());
                            break;
                    }
                }
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" [NAS] handle packet error", e);
                }
            }
        }

        @Override
        protected ByteBuffer createReceiveBuffer() {
            return ByteBuffer.allocate(Packet.MAX_LENGTH);
        }

        /**
         * {@inheritDoc}
         *
         * <p>Since {@link AbstractDatagramServer} assumes that each incoming packet will
         * be received once, we only check if that packet is a valid huawei portal
         * request packet.
         *
         * @param buffer incoming datagram buffer.
         * @return true if incoming datagram packet is a valid huawei portal request.
         * @throws IOException
         */
        @Override
        protected boolean verifyPacket(ByteBuffer buffer) throws IOException {
            byte[] data = buffer.array();
            /* HUAWEI V1 and V2 has a minimum length at 16. */
            return data.length > 16 && data[0] == endpoint.getVersion().value() &&
                    (endpoint.getVersion() == Version.V2 &&
                            ByteBufferCodecFactory.verify(buffer, endpoint.getSharedSecret()));
        }
    }
}
