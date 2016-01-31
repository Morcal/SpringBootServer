package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.nio.support.AbstractDatagramServer;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.*;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default portal server.
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
    private final HuaweiCodecFactory codecFactory;

    /** Internal datagram server. */
    private final DatagramPortalServer datagramPortalServer;

    private final ServerHandler handler;

    Endpoint endpoint;

    private final AtomicInteger reqId = new AtomicInteger(0);

    HuaweiPortalServer(Endpoint endpoint, ServerHandler handler) {
        this(endpoint, handler, DEFAULT_THREAD_SIZE);
    }

    HuaweiPortalServer(Endpoint endpoint, ServerHandler handler, int threadSize) {
        this.handler = handler;
        this.codecFactory = new HuaweiCodecFactory();
        this.endpoint = endpoint;
        this.datagramPortalServer
                = new DatagramPortalServer(endpoint.getPort(), threadSize);
    }

    /**
     * Get next request id.
     * @return request id.
     */
    private int nextReqId() {
        return reqId.updateAndGet(i -> (i >= Short.MAX_VALUE - 1 ? 0 : i + 1));
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
     * Handle incoming challenge requst.
     * @param channel datagram channel.
     * @param request request.
     * @param remote remote address.
     * @throws IOException
     */
    private void handleChallenge(DatagramChannel channel,
                                 HuaweiPacket request,
                                 SocketAddress remote) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] handle challenge.");
        }

        int req = request.getReqId();
        String ip = ipHexString(request.getIp());
        List<String> results = new ArrayList<>();
        ChallengeError error = handler.challenge(ip, req, results);

        HuaweiPacket ack;
        if (error ==  ChallengeError.OK) {
            String challenge = results.get(0);
            ack = Packets.newChallengeAck(
                    endpoint.getAddress(), challenge, nextReqId(), error, request);
            if (logger.isDebugEnabled()) {
                logger.debug("[NAS] challenge created: {}.", challenge);
            }
        } else {
            ack = Packets.newChallengeAck(
                    endpoint.getAddress(), "", nextReqId(), error, request);
        }

        channel.send(codecFactory.getEncoder()
                        .encode(request.getAuthenticator(), ack, endpoint.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    /**
     * Handle incoming authentication request.
     * @param channel datagram channel.
     * @param request incoming request.
     * @param remote remote address.
     * @throws IOException
     */
    private void handleAuth(DatagramChannel channel,
                            HuaweiPacket request,
                            SocketAddress remote) throws IOException {

        int requestId = request.getReqId();
        AuthType authType = AuthType.valueOf(request.getAuthType());

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] handle authentication {}", authType);
        }

        String ip = ipHexString(request.getIp());

        Collection<HuaweiPacket.Attribute> attributes = request.getAttributes();
        Optional<HuaweiPacket.Attribute> username = attributes.stream()
                .filter(attr -> attr.getType() == AttributeType.USER_NAME.code())
                .findFirst();

        AuthError error = AuthError.OK;
        do {
            if (!username.isPresent()) {
                error = AuthError.FAILED;
                break;
            }

            String user = new String(username.get().getValue()), password = null;
            switch (AuthType.valueOf(request.getAuthType())) {
                case CHAP:
                    Optional<HuaweiPacket.Attribute> chapPwd = attributes.stream()
                            .filter(attr -> attr.getType() == AttributeType.CHALLENGE_PASSWORD.code())
                            .findFirst();
                    if (!chapPwd.isPresent()) {
                        error = AuthError.FAILED;
                        break;
                    }
                    password = new String(chapPwd.get().getValue());
                    break;

                case PAP:
                    Optional<HuaweiPacket.Attribute> pwd = attributes.stream()
                            .filter(attr -> attr.getType() == AttributeType.PASSWORD.code())
                            .findFirst();
                    if (pwd.isPresent()) {
                        error = AuthError.FAILED;
                        break;
                    }
                    password = new String(pwd.get().getValue());
                    break;
            }

            if (error == AuthError.OK) {
                //FIXME collect mac address from request.
                Credentials credentials = Credentials.of(user, password, ip, "");
                error = handler.authenticate(requestId, credentials, authType);
            }
        } while (false);

        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newAuthAck(endpoint.getAddress(), nextReqId(), error, request),
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
                              HuaweiPacket request,
                              SocketAddress remote) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] handle logout.");
        }

        String ip = ipHexString(request.getIp());
        Credentials credentials = Credentials.of("", "", ip, "");
        LogoutError error = handler.logout(credentials);
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
                                 HuaweiPacket request,
                                 SocketAddress remote) throws IOException {
        byte[] ip = request.getIp();
        //byte[] mac = in.getAttribute(Enums.AttributeType.USER_MAC);
        String userIp = InetAddress.getByAddress(ip).getHostAddress();
        String nasIp = ((InetSocketAddress) remote).getAddress().getHostAddress();
        LogoutError error = handler.ntfLogout(nasIp, userIp);
        HuaweiPacket ack = Packets.newNtfLogoutAck(request, error);
        ByteBuffer buf = codecFactory.getEncoder()
                .encode(request.getAuthenticator(), ack, endpoint.getSharedSecret());
        channel.send(buf, remote);
    }

    /** Internal datagram server. */
    class DatagramPortalServer extends AbstractDatagramServer {
        DatagramPortalServer(int port, int threadSize) {
            super(port, threadSize, "portal-server");
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                HuaweiPacket in = codecFactory.getDecoder()
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
                            logger.debug("[NAS] Authentication affirmative acknowledged received.");
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
            return ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
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
            return data.length > 16 && data[0] != endpoint.getVersion().value() &&
                    (endpoint.getVersion() == Version.V2 && HuaweiCodecFactory.verify(buffer, endpoint.getSharedSecret()));
        }
    }
}
