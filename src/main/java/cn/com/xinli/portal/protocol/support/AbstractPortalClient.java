package cn.com.xinli.portal.protocol.support;

import cn.com.xinli.portal.AuthType;
import cn.com.xinli.portal.Message;
import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.Packet;
import cn.com.xinli.portal.protocol.PortalClient;
import cn.com.xinli.portal.protocol.UnsupportedAuthenticationTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Abstract portal client.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public abstract class AbstractPortalClient implements PortalClient {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractPortalClient.class);

    /** Portal client authentication type. */
    protected final AuthType authType;

    public AbstractPortalClient(AuthType authType) {
        this.authType = authType;
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
    protected abstract Packet createChapReqPacket(Credentials credentials) throws IOException;

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
     * @param credentials user credentials.
     * @return logout request packet, or null if ip address in credentials is unknown.
     */
    protected abstract Packet createLogoutPacket(Credentials credentials) throws IOException;

    /**
     * Handle CHAP request not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     */
    protected abstract Message<?> onChapRequestNotRespond(Packet request) throws IOException;

    /**
     * Handle authentication request not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     */
    protected abstract Message<?> onAuthenticationNotRespond(Packet request) throws IOException;

    /**
     * Handle authentication response.
     * @param response response.
     * @return message.
     * @throws IOException
     */
    protected abstract Message<?> onAuthenticationResponse(Packet response) throws IOException;

    /**
     * Handle logout response.
     * @param response response.
     * @return message.
     * @throws IOException
     */
    protected abstract Message<?> onLogoutResponse(Packet response) throws IOException;

    /**
     * Handle logout not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     */
    protected abstract Message<?> onLogoutNotRespond(Packet request) throws IOException;

    private boolean validateCredentials(Credentials credentials) {
        if (credentials == null) {
            return false;
        }

        String ip = credentials.getIp();

        try {
            return InetAddress.getByName(ip) != null;
        } catch (UnknownHostException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Invalid ip: {}.", ip);
            }
            return false;
        }
    }

    @Override
    public Message<?> login(Credentials credentials) throws IOException {
        if (!validateCredentials(credentials)) {
            return Message.of(null, false, "Invalid credentials.");
        }

        Optional<Packet> response;
        Packet request;

        switch (authType) {
            case CHAP:
                Packet challenge = createChapReqPacket(credentials);
                response = request(challenge);
                if (!response.isPresent()) {
                    /* Not respond, send timeout NAK, reqId = 0. */
                    return onChapRequestNotRespond(challenge);
                }

                Packet chapAck = response.get();
                request = createChapAuthPacket(chapAck, credentials);
                response = request(request);
                break;

            case PAP:
                request = createPapAuthPacket(credentials);
                response = request(request);
                break;

            default:
                throw new UnsupportedAuthenticationTypeException(authType);
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            logger.debug("> Handle authentication response.");
            return onAuthenticationResponse(response.get());
        } else {
            logger.debug("> Handle authentication timeout.");
            return onAuthenticationNotRespond(request);
        }
    }

    @Override
    public Message<?> logout(Credentials credentials) throws IOException {
        if (!validateCredentials(credentials)) {
            return Message.of(null, false, "Invalid credentials.");
        }

        /* Create portal request to logout. */
        Packet logout = createLogoutPacket(credentials);
        if (logout == null) {
            logger.warn("+ Failed to create logout.");
            return Message.of(null ,false, "Failed to create logout request.");
        }

        Optional<Packet> response = request(logout);

        if (!response.isPresent()) {
            logger.debug("> Handle logout timeout.");
            return onLogoutNotRespond(logout);
        } else {
            logger.debug("> Handle logout response.");
            return onLogoutResponse(response.get());
        }
    }
}
