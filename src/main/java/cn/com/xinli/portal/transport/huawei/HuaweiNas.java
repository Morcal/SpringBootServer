package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.AuthType;
import cn.com.xinli.portal.core.Nas;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.support.AbstractDatagramServer;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock Huawei NAS.
 *
 * <p>Mocked huawei nas supports Huawei portal protocol V1, V2.
 * By default, this nas listens on port 2001.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
final class HuaweiNas implements PortalServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiNas.class);

    private final AtomicInteger reqId = new AtomicInteger(0);

    private Map<String, Integer> requestMapping = new ConcurrentHashMap<>();

    private Map<Integer, Challenge> challengeMapping = new ConcurrentHashMap<>();

    private Map<String, String> userCredentials = new HashMap<>();

    private final Nas nas;

    private final InMemorySessionService sessionService;

    private final HuaweiCodecFactory codecFactory;

    private final PortalServer portalServer;

    private final ExecutorService executorService;

    private volatile boolean shutdown = false;

    private static final int CHALLENGE_TTL = 60; // seconds.

    private final PriorityBlockingQueue<Challenge> challenges;

    private final static AtomicLong challengeId = new AtomicLong(0);

    private final Object challengeSignal = new Object();

    /** Default worker thread size. */
    private static final int DEFAULT_THREAD_SIZE = 4;

    public HuaweiNas(Nas nas) {
        this.nas = nas;
        this.codecFactory = new HuaweiCodecFactory();
        this.sessionService = new InMemorySessionService();
        for (int i = 0; i < 10; i++) {
            userCredentials.put("test" + i, "test" + i);
        }
        this.portalServer = new PortalServer(nas.getListenPort());
        this.executorService = Executors.newCachedThreadPool();
        this.challenges = new PriorityBlockingQueue<>(256, new ChallengeComparator());
    }

    /**
     * Get next request id.
     * @return request id.
     */
    private int nextReqId() {
        return reqId.updateAndGet(i -> (i >= Short.MAX_VALUE - 1 ? 0 : i + 1));
    }

    /**
     * Convert ip address in a byte array to a hex string.
     * @param ip ip address.
     * @return hex string.
     */
    private static String ipHexString(byte[] ip) {
        return new String(Hex.encodeHex(ip));
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
        requestMapping.remove(ip);
        Session session = sessionService.removeSession(ip);
        LogoutError error = session == null ? LogoutError.GONE : LogoutError.OK;
        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newLogoutAck(nas.getNetworkAddress(), error, request),
                        nas.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    /**
     * Perform authentication.
     * @param request incoming request.
     * @return authentication error.
     * @throws IOException
     */
    private AuthError authenticate(HuaweiPacket request) throws IOException {
        Collection<HuaweiPacket.Attribute> attributes = request.getAttributes();
        Optional<HuaweiPacket.Attribute> username = attributes.stream()
                .filter(attr -> attr.getType() == AttributeType.USER_NAME.code())
                .findFirst();

        if (!username.isPresent())
            return AuthError.FAILED;

        String user = new String(username.get().getValue()),
                passwd = userCredentials.get(user);

        boolean authenticated = false;
        switch (AuthType.valueOf(request.getAuthType())) {
            case CHAP:
                Optional<HuaweiPacket.Attribute> chapPwd = attributes.stream()
                        .filter(attr -> attr.getType() == AttributeType.CHALLENGE_PASSWORD.code())
                        .findFirst();
                Challenge challenge = challengeMapping.get(request.getReqId());
                if (challenge == null) {
                    logger.warn("* [NAS] challenge not found.");
                    return AuthError.REJECTED;
                }

                authenticated = chapPwd.isPresent() && passwd != null &&
                        Arrays.equals(Packets.newChapPassword(
                                request.getReqId(),
                                userCredentials.get(user),
                                challenge.value.getBytes()), chapPwd.get().getValue());
                break;

            case PAP:
                Optional<HuaweiPacket.Attribute> password = attributes.stream()
                        .filter(attr -> attr.getType() == AttributeType.PASSWORD.code())
                        .findFirst();
                authenticated = password.isPresent() && passwd != null &&
                        StringUtils.equals(new String(password.get().getValue()), passwd);
                break;

            default:
                break;
        }

        return authenticated ? AuthError.OK : AuthError.REJECTED;
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
        Integer reqId;
        AuthType authType = AuthType.valueOf(request.getAuthType());
        String ip = ipHexString(request.getIp());

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] handle authentication {}", authType);
        }

        switch (authType) {
            case CHAP:
                reqId = requestMapping.get(ip);
                if (reqId == null) {
                    logger.warn("* [NAS] Can't find request mapping, ip: {}.", ip);
                    return;
                }

                if (reqId != request.getReqId()) {
                    logger.warn("* [NAS] mismatched request id, mapped: {}, incoming: {}",
                            reqId, request.getReqId());
                    return;
                }

                break;

            case PAP:
                reqId = nextReqId();
                break;

            default:
                logger.error("* [NAS] Unsupported authentication type, code: {}.",
                        request.getAuthType());
                return;
        }

        AuthError error = authenticate(request);
        if (authType == AuthType.CHAP) {
            /* CHAP finished, clean mapping. */
            requestMapping.remove(ip);
        }

        if (error == AuthError.OK) {
            sessionService.createSession(ip);
        }

        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newAuthAck(nas.getNetworkAddress(), reqId, error, request),
                        nas.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
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

        int reqId = nextReqId();
        String ip = ipHexString(request.getIp());

        Session session = sessionService.getSession(ip);
        HuaweiPacket ack;
        ChallengeError error;
        if (session != null) {
            error = ChallengeError.ALREADY_ONLINE;
            ack = Packets.newChallengeAck(
                    nas.getNetworkAddress(), "", request.getReqId(), error, request);
        } else {
            requestMapping.put(ip, reqId);

            if (logger.isDebugEnabled()) {
                logger.debug("[NAS] CHAP mapped, ip: {}.", ip);
            }

            /* Create challenge. */
            Challenge challenge = createChallenge(reqId);
            error = ChallengeError.OK;

            ack = Packets.newChallengeAck(
                    nas.getNetworkAddress(), challenge.value, reqId, error, request);
            if (logger.isDebugEnabled()) {
                logger.debug("[NAS] challenge created: {}.", challenge.value);
            }
        }

        channel.send(codecFactory.getEncoder()
                .encode(request.getAuthenticator(), ack, nas.getSharedSecret()),
                remote);

        if (logger.isDebugEnabled()) {
            logger.debug("[NAS] {} sent.", error.getDescription());
        }
    }

    private Challenge createChallenge(int reqId) {
        Challenge challenge = new Challenge(reqId);
        challengeMapping.put(reqId, challenge);
        challenges.offer(challenge);
        return challenge;
    }

    /**
     * Evict challenges.
     */
    private void evictChallenges()  {
        while (!shutdown) {
            try {
                Challenge challenge = challenges.take();
                if (challenge.isEmpty())
                    break;

                long now = System.currentTimeMillis();
                long remaining = CHALLENGE_TTL * 1000L - (now - challenge.createTime);
                if (remaining < 0L) {
                    /* Remove challenge mapping. */
                    logger.info("[NAS] challenge expired: {}.", challenge.reqId);
                    challengeMapping.remove(challenge.reqId);
                } else {
                    /*
                     * Put challenge back to queue.
                     * If other thread is checking mapping, it's ok.
                     */
                    challenges.offer(challenge);
                    synchronized (challengeSignal) {
                        challengeSignal.wait(remaining);
                    }
                }
            } catch (InterruptedException e) {
                logger.warn(e.getMessage());
                break;
            }
        }
        logger.info("[NAS] challenge eviction quit.");
    }

    @Override
    public void start() throws IOException {
        this.executorService.submit(this::evictChallenges);
        this.portalServer.start();
        logger.info("[NAS] Mock Huawei NAS (portal server) started, listen on port: {}.",
                nas.getListenPort());
    }

    @Override
    public void shutdown() {
        shutdown = true;
        logger.info("[NAS] Shutting down.");
        /* Wake up eviction by putting an empty challenge. */
        challenges.offer(Challenge.empty());

        synchronized (challengeSignal) {
            challengeSignal.notify();
        }

        portalServer.shutdown();
        try {
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(" [NAS] shutdown error", e);
        } finally {
            executorService.shutdownNow();
        }

        logger.info("[NAS] quit.");
    }

    /** CHAP challenge. */
    static class Challenge {
        int reqId;
        long id;
        long createTime;
        String value;

        private static final Challenge EMPTY = new Challenge(-1);

        static Challenge empty() {
            return EMPTY;
        }

        public Challenge(int reqId) {
            this.reqId = reqId;
            id = challengeId.incrementAndGet();
            createTime = System.currentTimeMillis();
            value = RandomStringUtils.randomAlphanumeric(16);
        }

        boolean isEmpty() {
            return equals(EMPTY);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Challenge challenge = (Challenge) o;
            return id == challenge.id && reqId == challenge.reqId &&
                    createTime == challenge.createTime && value.equals(challenge.value);
        }

        @Override
        public int hashCode() {
            int result = reqId;
            result = 31 * result + (int) (id ^ (id >>> 32));
            result = 31 * result + (int) (createTime ^ (createTime >>> 32));
            result = 31 * result + value.hashCode();
            return result;
        }
    }

    /**
     * Challenge comparator.
     */
    class ChallengeComparator implements Comparator<Challenge> {
        @Override
        public int compare(Challenge o1, Challenge o2) {
            if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("cant' compare with empty.");
            }

            return o1.createTime < o2.createTime ? -1 : 1;
        }
    }

    /**
     * Mock session.
     */
    class Session {
        long id;
        String ip;
        long nasId;
    }

    /**
     * In memory session service.
     */
    class InMemorySessionService {
        AtomicLong sessionId = new AtomicLong(0);

        private Map<String, Session> sessions = new ConcurrentHashMap<>();

        public Session createSession(String ip) {
            Session session = sessions.get(ip);
            if (session != null)
                return session;

            session = new Session();
            session.id = sessionId.incrementAndGet();
            session.ip = ip;
            session.nasId = nas.getId();

            sessions.put(ip, session);
            return session;
        }

        public Session getSession(String ip) {
            return sessions.get(ip);
        }

        public Session removeSession(String ip) {
            return sessions.remove(ip);
        }

    }

    /**
     * Huawei NAS built-in portal server.
     */
    class PortalServer extends AbstractDatagramServer {
        PortalServer(int port) {
            super(port, DEFAULT_THREAD_SIZE, "huawei-nas");
        }

        /**
         * {@inheritDoc}
         *
         * Since {@link AbstractDatagramServer} assumes that incoming packet will
         * be received once, we only check if that packet is a valid huawei portal
         * request packet.
         * @param buffer incoming datagram buffer.
         * @return true if incoming datagram packet is a valid huawei portal request.
         * @throws IOException
         */
        @Override
        protected boolean verifyPacket(ByteBuffer buffer) throws IOException {
            byte[] data = buffer.array();
            /* Huawei V1 and V2 has a minimum length at 16. */
            return !(data.length < 16) &&
                    (data[0] != Version.V2.value() ||
                            HuaweiCodecFactory.verify(buffer, nas.getSharedSecret()));
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                HuaweiPacket in = codecFactory.getDecoder()
                        .decode(buffer, nas.getSharedSecret());
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
    }
}
