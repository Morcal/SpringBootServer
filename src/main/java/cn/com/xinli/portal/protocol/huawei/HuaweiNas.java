package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.SessionNotFoundException;
import cn.com.xinli.portal.SessionService;
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
        super(createSessionService());
    }

    static SessionService createSessionService() {
        return new SessionService() {
            @Override
            public Session createSession(Nas nas, Session session) throws IOException {
                return null;
            }

            @Override
            public Session getSession(long id) throws SessionNotFoundException {
                return null;
            }

            @Override
            public void removeSession(long id) throws SessionNotFoundException {}

            @Override
            public Optional<Session> find(String ip, String mac) {
                return Optional.empty();
            }

            @Override
            public Session update(long id, long timestamp) {
                return null;
            }

            @Override
            public void removeSession(String ip) {
                log.debug("> Removing session, ip: " + ip);
            }
        };
    }

    @Override
    protected void handlePacket(DatagramPacket packet) {
        try {
            Packet in = codecFactory.getDecoder().decode(packet, sharedSecret);
            if (codecFactory.verify(in, sharedSecret)) {
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
