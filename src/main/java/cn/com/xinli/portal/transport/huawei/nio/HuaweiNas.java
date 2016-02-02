package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Mock HUAWEI NAS.
 *
 * <p>Mocked huawei nas supports HUAWEI portal protocol V1, V2.
 * By default, this nas listens on port 2001.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
final class HuaweiNas implements PortalServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiNas.class);

    private Map<String, Integer> requestMapping = new ConcurrentHashMap<>();

    private Map<Integer, Challenge> challengeMapping = new ConcurrentHashMap<>();

    private Map<String, String> userCredentials = new HashMap<>();

    private final Endpoint endpoint;

    private final InMemorySessionService sessionService;

    private final HuaweiPortalServer portalServer;

    private final ExecutorService executorService;

    private volatile boolean shutdown = false;

    private static final int CHALLENGE_TTL = 60; // seconds.

    private final PriorityBlockingQueue<Challenge> challenges;

    private final Object challengeSignal = new Object();

    private static final String EMPTY_PASSWORD_SUBSTITUTION = "***";

    public HuaweiNas(Endpoint endpoint) {
        this.endpoint = endpoint;
        this.sessionService = new InMemorySessionService();
        for (int i = 0; i < 10; i++) {
            userCredentials.put("test" + i, "test" + i);
        }
        if (endpoint.getPort() == 0) {
            endpoint.setPort(Endpoint.DEFAULT_LISTEN_PORT);
        }
        this.portalServer = new HuaweiPortalServer(endpoint, new Handler());
        this.executorService = Executors.newCachedThreadPool();
        this.challenges = new PriorityBlockingQueue<>(256, new ChallengeComparator());
    }

    /**
     * Perform authentication.
     *
     * @param requestId   incoming request id.
     * @param credentials credentials.
     * @param authType    authentication type.
     * @return authentication error.
     * @throws IOException
     */
    private AuthError authenticate(int requestId, Credentials credentials, AuthType authType) throws IOException {
        final String user = credentials.getUsername(),
                localPassword = userCredentials.get(user);
        String password = StringUtils.defaultString(credentials.getPassword(), EMPTY_PASSWORD_SUBSTITUTION);

        boolean authenticated = false;
        switch (authType) {
            case CHAP:
                Challenge challenge = challengeMapping.get(requestId);
                if (challenge == null) {
                    logger.warn("* [NAS] challenge not found.");
                    return AuthError.REJECTED;
                }

                authenticated =
                        StringUtils.equals(Hex.encodeHexString(Packets.newChapPassword(
                                requestId,
                                StringUtils.defaultString(
                                        userCredentials.get(user), EMPTY_PASSWORD_SUBSTITUTION).getBytes(),
                                challenge.getValue().getBytes())),
                                password);
                break;

            case PAP:
                //TODO make sure UTF-8 encoding works.
                authenticated = StringUtils.equals(password, localPassword);
                break;

            default:
                break;
        }

        return authenticated ? AuthError.OK : AuthError.REJECTED;
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
    private void evictChallenges() {
        while (!shutdown) {
            try {
                Challenge challenge = challenges.take();
                if (challenge.isEmpty())
                    break;

                long now = System.currentTimeMillis();
                long remaining = CHALLENGE_TTL * 1000L - (now - challenge.getCreateTime());
                if (remaining < 0L) {
                    /* Remove challenge mapping. */
                    logger.info("[NAS] challenge expired: {}.", challenge.getReqId());
                    challengeMapping.remove(challenge.getReqId());
                    /* Remove requests mapping as well. */
                    portalServer.release(challenge.getReqId());
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
        logger.info("[NAS] Mock HUAWEI NAS (portal server) started, listen on endpoint: {}.",
                endpoint);
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

    /**
     * Challenge comparator.
     */
    class ChallengeComparator implements Comparator<Challenge> {
        @Override
        public int compare(Challenge o1, Challenge o2) {
            if (o1 == null || o2 == null) {
                throw new IllegalArgumentException("cant' compare with empty.");
            }

            return o1.getCreateTime() < o2.getCreateTime() ? -1 : 1;
        }
    }

    /**
     * Mock session.
     */
    class Session {
        long id;
        String ip;
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
     * HUAWEI NAS portal server handler.
     */
    class Handler implements ServerHandler {
        @Override
        public ChallengeError challenge(String ip, int requestId, Collection<String> results) {
            Session session = sessionService.getSession(ip);
            if (session != null) {
                return ChallengeError.ALREADY_ONLINE;
            } else {
                requestMapping.put(ip, requestId);

                if (logger.isDebugEnabled()) {
                    logger.debug("[NAS] CHAP mapped, ip: {}.", ip);
                }

                /* Create challenge. */
                Challenge challenge = createChallenge(requestId);
                results.add(challenge.getValue());
                return ChallengeError.OK;
            }
        }

        @Override
        public AuthError authenticate(int requestId, Credentials credentials, AuthType authType) throws IOException {
            String ip = credentials.getIp();
            Integer reqId;
            switch (authType) {
                case CHAP:
                    reqId = requestMapping.get(ip);
                    if (reqId == null) {
                        logger.warn("* [NAS] Can't find request mapping, ip: {}.", ip);
                        return AuthError.FAILED;
                    }

                    if (reqId != requestId) {
                        logger.warn("* [NAS] mismatched request id, mapped: {}, incoming: {}",
                                reqId, requestId);
                        return AuthError.FAILED;
                    }

                    break;

                case PAP:
                    break;

                default:
                    logger.error("* [NAS] Unsupported authentication type, code: {}.", authType);
                    return AuthError.REJECTED;
            }

            AuthError error = HuaweiNas.this.authenticate(requestId, credentials, authType);
            if (authType == AuthType.CHAP) {
                /* CHAP finished, clean mapping. */
                requestMapping.remove(ip);
            }

            if (error == AuthError.OK) {
                sessionService.createSession(ip);
            } else {
                if (logger.isTraceEnabled()) {
                    logger.trace("authentication failed, credentials: {}", credentials);
                }
            }

            return error;
        }

        @Override
        public LogoutError logout(Credentials credentials) throws IOException {
            String ip = credentials.getIp();
            requestMapping.remove(ip);
            Session session = sessionService.removeSession(ip);
            return session == null ? LogoutError.GONE : LogoutError.OK;
        }

        @Override
        public LogoutError ntfLogout(String nasIp, String userIp) throws IOException {
            logger.warn("not supported");
            return LogoutError.REJECTED;
        }
    }
}
