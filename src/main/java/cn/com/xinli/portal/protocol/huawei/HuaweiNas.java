package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.AuthType;
import cn.com.xinli.portal.protocol.support.AbstractDatagramServer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.crypto.codec.Hex;

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
 * <p>Mocked huawei nas supports Huawei portal protocol v1, v2.
 * By default, this nas listens on port 2001.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
public class HuaweiNas {
    /** Log. */
    private static final Log log = LogFactory.getLog(HuaweiNas.class);

    private static final AtomicInteger reqId = new AtomicInteger(0);

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
    private static int nextReqId() {
        return reqId.updateAndGet(i -> (i >= Short.MAX_VALUE - 1 ? 0 : i + 1));
    }

    /**
     * Convert ip address in a byte array to a hex string.
     * @param ip ip address.
     * @return hex string.
     */
    private static String ipHexString(byte[] ip) {
        return new String(Hex.encode(ip));
    }

    private void handleLogout(DatagramChannel channel,
                              HuaweiPacket request,
                              SocketAddress remote) throws IOException {
        String ip = ipHexString(request.getIp());
        requestMapping.remove(ip);
        Session session = sessionService.removeSession(ip);
        Enums.LogoutError error = session == null ? Enums.LogoutError.GONE : Enums.LogoutError.OK;
        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newLogoutAck(nas.getInetAddress(), error, request),
                        nas.getSharedSecret()),
                remote);

        if (log.isDebugEnabled()) {
            log.debug("> [NAS] logout, " + error.getDescription() + ", sent.");
        }
    }

    private Enums.AuthError authenticate(HuaweiPacket request) throws IOException {
        Collection<HuaweiPacket.Attribute> attributes = request.getAttributes();
        Optional<HuaweiPacket.Attribute> username = attributes.stream()
                .filter(attr -> attr.getType() == Enums.Attribute.USER_NAME.code())
                .findFirst();

        if (!username.isPresent())
            return Enums.AuthError.FAILED;

        String user = new String(username.get().getValue()),
                passwd = userCredentials.get(user);

        boolean authenticated = false;
        switch (AuthType.valueOf(request.getAuthType())) {
            case CHAP:
                Optional<HuaweiPacket.Attribute> chapPwd = attributes.stream()
                        .filter(attr -> attr.getType() == Enums.Attribute.CHALLENGE_PASSWORD.code())
                        .findFirst();
                Challenge challenge = challengeMapping.get(request.getReqId());
                if (challenge == null) {
                    log.warn("* [NAS] challenge not found.");
                    return Enums.AuthError.REJECTED;
                }

                authenticated = chapPwd.isPresent() && passwd != null &&
                        Arrays.equals(Packets.newChapPassword(
                                request.getReqId(),
                                userCredentials.get(user),
                                challenge.value.getBytes()), chapPwd.get().getValue());
                break;

            case PAP:
                Optional<HuaweiPacket.Attribute> password = attributes.stream()
                        .filter(attr -> attr.getType() == Enums.Attribute.PASSWORD.code())
                        .findFirst();
                authenticated = password.isPresent() && passwd != null &&
                        StringUtils.equals(new String(password.get().getValue()), passwd);
                break;

            default:
                break;
        }

        return authenticated ? Enums.AuthError.OK : Enums.AuthError.REJECTED;
    }

    private void handleAuth(DatagramChannel channel,
                            HuaweiPacket request,
                            SocketAddress remote) throws IOException {
        Integer reqId;
        AuthType authType = AuthType.valueOf(request.getAuthType());
        String ip = ipHexString(request.getIp());
        switch (authType) {
            case CHAP:
                reqId = requestMapping.get(ip);
                if (reqId == null) {
                    log.warn("* [NAS] Can't find request mapping, ip: " + ip + ".");
                    return;
                }

                if (reqId != request.getReqId()) {
                    log.warn("* [NAS] mismatched request id.");
                    return;
                }

                break;

            case PAP:
                reqId = nextReqId();
                break;

            default:
                log.error("* [NAS] Unsupported authentication type, code: " + request.getAuthType());
                return;
        }

        Enums.AuthError error = authenticate(request);

        if (error == Enums.AuthError.OK) {
            sessionService.createSession(ip);
        }

        channel.send(
                codecFactory.getEncoder().encode(
                        request.getAuthenticator(),
                        Packets.newAuthAck(nas.getInetAddress(), reqId, error, request),
                        nas.getSharedSecret()),
                remote);

        if (log.isDebugEnabled()) {
            log.debug("> [NAS] authentication " + error.getDescription() + " sent.");
        }
    }

    private void handleChallenge(DatagramChannel channel,
                                 HuaweiPacket request,
                                 SocketAddress remote) throws IOException {
        int reqId = nextReqId();
        String ip = ipHexString(request.getIp());

        Session session = sessionService.getSession(ip);
        HuaweiPacket ack;
        Enums.ChallengeError error;
        if (session != null) {
            error = Enums.ChallengeError.ALREADY_ONLINE;
            ack = Packets.newChallengeAck(
                    nas.getInetAddress(), "", request.getReqId(), error, request);
        } else {
            requestMapping.put(ip, reqId);

            if (log.isDebugEnabled()) {
                log.debug("> [NAS] CHAP mapped, ip: " + ip + ", response sent.");
            }

            /* Create challenge. */
            Challenge challenge = createChallenge(reqId);
            error = Enums.ChallengeError.OK;

            ack = Packets.newChallengeAck(
                    nas.getInetAddress(), challenge.value, reqId, error, request);
            if (log.isDebugEnabled()) {
                log.debug("> [NAS] challenge created: " + challenge.value);
            }
        }

        channel.send(
                codecFactory.getEncoder().encode(request.getAuthenticator(), ack, nas.getSharedSecret()),
                remote);

        if (log.isDebugEnabled()) {
            log.debug("> [NAS] challenge " + error.getDescription() + " sent.");
        }
    }

    private Challenge createChallenge(int reqId) {
        Challenge challenge = new Challenge(reqId);
        challengeMapping.put(reqId, challenge);
        challenges.offer(challenge);
        return challenge;
    }

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
                    log.info("> [NAS] challenge expired: " + challenge.reqId);
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
                log.warn(e.getMessage());
                break;
            }
        }
        log.info("> [NAS] challenge eviction quit.");
    }

    public void start() throws IOException {
        this.executorService.submit(this::evictChallenges);
        this.portalServer.start();
        log.info("> [NAS] Mock Huawei NAS (portal server) started, listen on port: " + nas.getListenPort() + ".");
    }

    public void shutdown() {
        shutdown = true;
        log.info("> [NAS] Shutting down.");
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
            log.error(e);
        } finally {
            executorService.shutdownNow();
        }

        log.info("> [NAS] quit.");
    }



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
            return id == challenge.id && reqId == challenge.reqId && createTime == challenge.createTime && value.equals(challenge.value);
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

    class ChallengeComparator implements Comparator<Challenge> {
        @Override
        public int compare(Challenge o1, Challenge o2) {
            if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("cant' compare with empty.");
            }

            return o1.createTime < o2.createTime ? -1 : 1;
        }
    }

    class InMemorySessionService {
        AtomicLong sessionId = new AtomicLong(0);

        private Map<String, Session> sessions = new ConcurrentHashMap<>();

        public Session createSession(String ip) {
            Session session = sessions.get(ip);
            if (session != null)
                return session;

            SessionEntity entity = new SessionEntity();
            entity.setId(sessionId.incrementAndGet());
            entity.setDevice(ip);
            entity.setNasId(nas.getId());

            sessions.put(ip, entity);
            return entity;
        }

        public Session getSession(String ip) {
            return sessions.get(ip);
        }

        public Session removeSession(String ip) {
            return sessions.remove(ip);
        }

    }

    class PortalServer extends AbstractDatagramServer {
        PortalServer(int port) {
            super(port);
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
            /* Huawei v1 and v2 has a minimum length at 16. */
            return !(data.length < 16) &&
                    (data[0] != Enums.Version.v2.value() || HuaweiCodecFactory.verify(buffer, nas.getSharedSecret()));
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {
            try {
                HuaweiPacket in = codecFactory.getDecoder().decode(buffer, nas.getSharedSecret());
                Optional<Enums.Type> type = Enums.Type.valueOf(in.getType());
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
                            log.debug("> [NAS] Authentication affirmative acknowledged received.");
                            break;

                        default:
                            log.warn("> [NAS] Unsupported operation type: " + type.get().name());
                            break;
                    }
                }
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }
        }

        @Override
        protected ByteBuffer createReceiveBuffer() {
            return ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
        }


    }
}
