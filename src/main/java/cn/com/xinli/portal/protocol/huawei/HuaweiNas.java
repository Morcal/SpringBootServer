package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.protocol.Packet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Optional;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/23.
 */
public class HuaweiNas extends DefaultPortalServer {
    /** Log. */
    private static final Log log = LogFactory.getLog(HuaweiNas.class);

    public HuaweiNas() {
        super(createServerConfig(), createMockSessionService());
    }

    static ServerConfig createServerConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setPortalServerSharedSecret("s3cr3t");
        serverConfig.setPortalServerHuaweiVersion("v2");
        serverConfig.setPortalServerListenPort(2000);
        serverConfig.setPortalServerThreadSize(4);
        return serverConfig;
    }

    static SessionService createMockSessionService() {
        return new SessionService() {
            @Override
            public Message<Session> createSession(Nas nas, Session session) throws IOException {
                return Message.of(session, true, "");
            }

            @Override
            public Session getSession(long id) throws SessionNotFoundException {
                return null;
            }

            @Override
            public Message<Session> removeSession(long id) throws SessionNotFoundException {
                return Message.of(null, true, "");
            }

            @Override
            public Optional<Session> find(String ip, String mac) {
                return Optional.empty();
            }

            @Override
            public Session update(long id, long timestamp) {
                return null;
            }

            @Override
            public Message removeSession(String ip) {
                log.debug("> Removing session, ip: " + ip);
                return Message.of(null, true, "");
            }
        };
    }

    @Override
    protected void handlePacket(DatagramPacket packet) {
        try {
            if (codecFactory.verify(packet, sharedSecret)) {
                Packet in = codecFactory.getDecoder().decode(packet, sharedSecret);
                Optional<Enums.Type> type = Enums.Type.valueOf(in.getType());
                if (type.isPresent()) {
                    switch (type.get()) {
                        case REQ_CHALLENGE:
                            handleChallenge(in);
                            break;
                        
                        case REQ_AUTH:
                            handleAuth(in);
                            break;

                        case REQ_LOGOUT:
                            handleLogout(in);
                            break;

                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e);
            }
        }
        super.handlePacket(packet);
    }

    private void handleLogout(Packet in) {

    }

    private void handleAuth(Packet in) {

    }

    private void handleChallenge(Packet in) {
        
    }
}
