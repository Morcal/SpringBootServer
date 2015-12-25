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
import java.util.concurrent.ConcurrentHashMap;
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

    private Map<byte[], Integer> requestMapping = new ConcurrentHashMap<>();

    private Map<String, String> userCredentials = new HashMap<>();

    private final Nas nas;

    private final InMemorySessionService sessionService;

    private final CodecFactory codecFactory;

    private final PortalServer portalServer;

    public HuaweiNas(Nas nas) {
        this.nas = nas;
        this.codecFactory = new HuaweiCodecFactory();
        this.sessionService = new InMemorySessionService();
        for (int i = 0; i < 10; i++) {
            userCredentials.put("test" + i, "test" + i);
        }
        this.portalServer = new PortalServer(nas.getListenPort());
    }

    private void handleLogout(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) throws IOException {
        requestMapping.remove(in.getIp());
        Session session = sessionService.removeSession(in.getIp());
        Packet response = Utils.createLogoutResponsePacket(nas.getInetAddress(), session, in);
        DatagramPacket out = codecFactory.getEncoder()
                .encode(in.getAuthenticator(), response, remote, port, nas.getSharedSecret());
        socket.send(out);
    }

    private boolean authenticate(HuaweiPacket in) {
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
                authenticated = chapPwd.isPresent() && passwd != null &&
                        Arrays.equals(Utils.md5sum(
                                userCredentials.get(user).getBytes()), chapPwd.get().getValue());
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

    private void handleAuth(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) {
        Integer reqId;
        AuthType authType = AuthType.valueOf(in.getAuthType());
        switch (authType) {
            case CHAP:
                reqId = requestMapping.get(in.getIp());
                if (reqId == null) {
                    log.warn("* Can't find request mapping.");
                    return;
                }
                break;

            case PAP:
                reqId = HuaweiNas.reqId.incrementAndGet();
                break;

            default:
                log.error("* Unsupported authentication type, code: " + in.getAuthType());
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
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void handleChallenge(DatagramSocket socket, HuaweiPacket in, InetAddress remote, int port) throws IOException {
        int reqId = HuaweiNas.reqId.incrementAndGet();
        requestMapping.put(in.getIp(), reqId);
        /* H3C vBRAS will response with a bas ip attribute. */
        String challenge = RandomStringUtils.randomAlphanumeric(8);

        Packet response = Utils.createChallengeResponsePacket(nas.getInetAddress(), challenge, reqId, in);
        DatagramPacket out = codecFactory.getEncoder()
                .encode(in.getAuthenticator(), response, remote, port, nas.getSharedSecret());
        socket.send(out);
    }

    public void start() throws IOException {
        this.portalServer.start();
        log.info("> Mock Huawei NAS (portal server) started, listen on port: " + nas.getListenPort() + ".");
    }

//    public void shutdown() {
//        this.portalServer.shutdown();
//    }

    class InMemorySessionService {
        AtomicLong sessionId = new AtomicLong(0);

        private Map<byte[], Session> sessions = new ConcurrentHashMap<>();

        public Session createSession(byte[] ip) {
            Session session = sessions.get(ip);
            if (session != null)
                return session;

            SessionEntity entity = new SessionEntity();
            entity.setId(sessionId.incrementAndGet());
            entity.setDevice(new String(ip));
            entity.setNasId(nas.getId());

            sessions.put(ip, entity);
            return entity;
        }

        public Session removeSession(byte[] ip) {
            return sessions.remove(ip);
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
                            log.debug("> Authentication affirmative acknowledged received.");
                            break;

                        default:
                            log.warn("> Unsupported operation type: " + type.get().name());
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
