package cn.com.xinli.portal.protocol.support;

import cn.com.xinli.portal.Message;
import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Abstract portal client.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public abstract class AbstractPortalClient implements PortalClient {
    /** Log. */
    private static final Log log = LogFactory.getLog(AbstractPortalClient.class);

    /** Associated NAS. */
    protected final Nas nas;

    public AbstractPortalClient(Nas nas) {
        this.nas = nas;
    }

    /**
     * Send a request to NAS and receive response.
     *
     * @param packet request packet.
     * @return response packet from NAS.
     * @throws IOException
     */
    protected abstract Optional<Packet> request(Packet packet) throws IOException;

    /**
     * Create challenge request packet.
     * @param credentials user credentials.
     * @return challenge request packet.
     */
    protected abstract Packet createChapReqPacket(Credentials credentials);

    /**
     * Create CHAP authentication packet.
     *
     * @param ack   challenge acknowledge packet.
     * @param credentials user credentials.
     * @return chap packet.
     */
    protected abstract Packet createChapAuthPacket(Packet ack, Credentials credentials) throws IOException;

    /**
     * Create PAP authentication packet.
     *
     * @param credentials user credentials.
     * @return PAP authentication packet.
     */
    protected abstract Packet createPapAuthPacket(Credentials credentials) throws IOException;

    /**
     * Create logout request packet.
     *
     * @param authType    authentication type.
     * @param credentials user credentials.
     * @return logout request packet, or null if ip address in credentials is unknown.
     */
    protected abstract Packet createLogoutPacket(AuthType authType, Credentials credentials) throws IOException;


    protected abstract Message<Packet> handleChapRequestNotRespond(Packet request) throws IOException;

    protected abstract Message<Packet> handleAuthenticationNotRespond(Packet request) throws IOException;

    protected abstract Message<Packet> handleAuthenticationResponse(Packet response) throws IOException;

    protected abstract Message<Packet> handleLogoutResponse(Packet response) throws IOException;

    protected abstract Message<Packet> handleLogoutNotRespond(Packet request) throws IOException;

    @Override
    public Message<Packet> login(Credentials credentials) throws IOException {
        AuthType authType = nas.getAuthType();
        Optional<Packet> response;
        Packet authRequest;

        switch (authType) {
            case CHAP:
                Packet challenge = createChapReqPacket(credentials);
                response = request(challenge);
                if (!response.isPresent()) {
                    /* Not respond, send timeout NAK, reqId = 0. */
                    return handleChapRequestNotRespond(challenge);
                }

                Packet chapAck = response.get();
                authRequest = createChapAuthPacket(chapAck, credentials);
                response = request(authRequest);
                break;

            case PAP:
                authRequest = createPapAuthPacket(credentials);
                response = request(authRequest);
                break;

            default:
                throw new PortalProtocolException("Unsupported authentication type: " + authType);
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            log.debug("> Handle authentication response.");
            return handleAuthenticationResponse(response.get());
        } else {
            log.debug("> Handle authentication timeout.");
            return handleAuthenticationNotRespond(authRequest);
        }
    }

    @Override
    public Message<Packet> logout(Credentials credentials) throws IOException {
        AuthType authType = nas.getAuthType();
        /* Create portal request to logout. */
        Packet logout = createLogoutPacket(authType, credentials);
        if (logout == null) {
            log.warn("+ Failed to create logout.");
            return Message.of(null ,false, "Failed to create logout request.");
        }

        Optional<Packet> response = request(logout);

        if (!response.isPresent()) {
            log.debug("> Handle logout timeout.");
            return handleLogoutNotRespond(logout);
        } else {
            log.debug("> Handle logout response.");
            return handleLogoutResponse(response.get());
        }
    }
}
