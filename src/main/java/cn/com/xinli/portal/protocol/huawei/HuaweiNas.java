package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.persist.SessionEntity;
import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Packet;
import cn.com.xinli.portal.protocol.support.AbstractDatagramServer;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

    private final CodecFactory codecFactory;

    private final PortalServer portalServer;

    private final ExecutorService executorService;

    private volatile boolean shutdown = false;

    private static final int CHALLENGE_TTL = 60; // seconds.

    private final PriorityBlockingQueue<Challenge> challenges;

    private final AtomicLong challengeId = new AtomicLong(0);

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

    private static int nextReqId() {
        return reqId.updateAndGet(i -> (i >= Short.MAX_VALUE - 1 ? 0 : i + 1));
    }

    private void handleLogout(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) throws IOException {
        requestMapping.remove(in.getIp());
        Session session = sessionService.removeSession(in.getIp());
        Packet response = Utils.createLogoutResponsePacket(nas.getInetAddress(), session, in);
        DatagramPacket out = codecFactory.getEncoder()
                .encode(in.getAuthenticator(), response, remote, port, nas.getSharedSecret());
        socket.send(out);

        if (log.isDebugEnabled()) {
            log.debug("> [NAS] logout, result: " + response.isSuccess() + ", sent.");
        }
    }

    private boolean authenticate(HuaweiPacket in) throws IOException {
        Collection<HuaweiPacket.Attribute> attributes = in.getAttributes();
        Optional<HuaweiPacket.Attribute> username = attributes.stream()
                .filter(attr -> attr.getType() == Enums.Attribute.USER_NAME.code())
                .findFirst();

        if (!username.isPresent())
            return false;

        String user = new String(username.get().getValue()),
                passwd = userCredentials.get(user);

        boolean authenticated = false;
        switch (AuthType.valueOf(in.getAuthType())) {
            case CHAP:
                Optional<HuaweiPacket.Attribute> chapPwd = attributes.stream()
                        .filter(attr -> attr.getType() == Enums.Attribute.CHALLENGE_PASSWORD.code())
                        .findFirst();
                Challenge challenge = challengeMapping.get(in.getReqId());
                if (challenge == null) {
                    log.warn("* [NAS] challenge not found.");
                    return false;
                }

                authenticated = chapPwd.isPresent() && passwd != null &&
                        Arrays.equals(Utils.createChapPassword(
                                in.getReqId(),
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

        return authenticated;
    }

    private void handleAuth(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) throws IOException {
        Integer reqId;
        AuthType authType = AuthType.valueOf(in.getAuthType());
        String ip = Utils.ipHexString(in.getIp());
        switch (authType) {
            case CHAP:
                reqId = requestMapping.get(ip);
                if (reqId == null) {
                    log.warn("* [NAS] Can't find request mapping, ip: " + ip + ".");
                    return;
                }

                if (reqId != in.getReqId()) {
                    log.warn("* [NAS] mismatched request id.");
                    return;
                }

                break;

            case PAP:
                reqId = nextReqId();
                break;

            default:
                log.error("* [NAS] Unsupported authentication type, code: " + in.getAuthType());
                return;
        }

        boolean authenticated = authenticate(in);

        if (authenticated) {
            sessionService.createSession(in.getIp());
        }

        Packet response = Utils.createAuthenticationResponsePacket(reqId, authenticated, in);

        try {
            DatagramPacket out = codecFactory.getEncoder()
                    .encode(
                            in.getAuthenticator(),
                            response,
                            remote,
                            port,
                            nas.getSharedSecret());
            socket.send(out);
            if (log.isDebugEnabled()) {
                log.debug("> [NAS] authenticated: " + authenticated + ", result sent.");
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void handleChallenge(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) throws IOException {
        int reqId = nextReqId();
        String ip = Utils.ipHexString(in.getIp());

        requestMapping.put(ip, reqId);
        /* Create challenge. */
        Challenge challenge = createChallenge(reqId);

        Packet response = Utils.createChallengeResponsePacket(nas.getInetAddress(), challenge.value, reqId, in);
        DatagramPacket out = codecFactory.getEncoder()
                .encode(in.getAuthenticator(), response, remote, port, nas.getSharedSecret());
        socket.send(out);

        if (log.isDebugEnabled()) {
            log.debug("> [NAS] CHAP mapped, ip: " + ip + ", response sent.");
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

                long now = System.currentTimeMillis();
                long remaining = CHALLENGE_TTL * 1000L - (now - challenge.createTime);
                if (remaining > 200L) {
                    /*
                     * Put challenge back to queue.
                     * If other thread is checking mapping, it's ok.
                     */
                    challenges.offer(challenge);
                    /* There's some time remaining, no need to poll that soon. */
                    Thread.sleep(remaining);
                } else if (remaining < 0L) {
                    /* Remove challenge mapping. */
                    challengeMapping.remove(challenge.reqId);
                }
            } catch (InterruptedException e) {
                log.warn(e.getMessage());
                break;
            }
        }
    }

    public void start() throws IOException {
        this.executorService.submit(this::evictChallenges);
        this.portalServer.start();
        log.info("> [NAS] Mock Huawei NAS (portal server) started, listen on port: " + nas.getListenPort() + ".");
    }

    public void shutdown() {
        this.shutdown = true;
        this.portalServer.shutdown();
        try {
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            executorService.shutdownNow();
        }

    }

    class Challenge {
        int reqId;
        long id;
        long createTime;
        String value;

        public Challenge(int reqId) {
            this.reqId = reqId;
            id = challengeId.incrementAndGet();
            createTime = System.currentTimeMillis();
            value = RandomStringUtils.randomAlphanumeric(16);
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

        public Session createSession(byte[] ip) {
            String tar = Utils.ipHexString(ip);
            Session session = sessions.get(ip);
            if (session != null)
                return session;

            SessionEntity entity = new SessionEntity();
            entity.setId(sessionId.incrementAndGet());
            entity.setDevice(new String(ip));
            entity.setNasId(nas.getId());

            sessions.put(tar, entity);
            return entity;
        }

        public Session removeSession(byte[] ip) {
            return sessions.remove(Utils.ipHexString(ip));
        }

    }

    class PortalServer extends AbstractDatagramServer {
        PortalServer(int port) {
            super(port);
        }

        @Override
        protected boolean verifyPacket(DatagramPacket packet) throws IOException {
            byte[] data = packet.getData();
            /* Huawei v1 and v2 has a minimum length at 16. */
            return !(data == null || data.length < 16) && (data[0] != V2.Version || HuaweiCodecFactory.verify(packet, nas.getSharedSecret()));
        }

        @Override
        protected void handlePacket(DatagramSocket socket, DatagramPacket packet) {
            try {
                HuaweiPacket in = (HuaweiPacket) codecFactory.getDecoder()
                        .decode(packet, nas.getSharedSecret());
                InetAddress remote = packet.getAddress();
                int port = packet.getPort();
                Optional<Enums.Type> type = Enums.Type.valueOf(in.getType());
                if (type.isPresent()) {
                    switch (type.get()) {
                        case REQ_CHALLENGE:
                            handleChallenge(socket, in, remote, port);
                            break;

                        case REQ_AUTH:
                            handleAuth(socket, in, remote, port);
                            break;

                        case REQ_LOGOUT:
                            handleLogout(socket, in, remote, port);
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
    }
}
