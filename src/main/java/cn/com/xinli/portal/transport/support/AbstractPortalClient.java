package cn.com.xinli.portal.transport.support;

import cn.com.xinli.portal.core.AuthType;
import cn.com.xinli.portal.core.Credentials;
import cn.com.xinli.portal.core.CredentialsException;
import cn.com.xinli.portal.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract portal client.
 *
 * <p>This class provides a very simple portal login/logout abstraction.
 * Classes extend this class can simply implements abstract methods much
 * like event handling.
 *
 * <p>Project: xpws
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
     * @throws PortalProtocolException
     */
    protected abstract Optional<Packet> request(Packet packet)
            throws IOException, PortalProtocolException;

    /**
     * Create challenge request packet.
     * @param credentials user credentials.
     * @return challenge request packet.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Packet createChapReqPacket(Credentials credentials)
            throws IOException, PortalProtocolException;

    /**
     * Create CHAP authentication packet.
     *
     * @param ack   challenge acknowledge packet.
     * @param credentials user credentials.
     * @return chap packet.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Packet createChapAuthPacket(Packet ack, Credentials credentials)
            throws IOException, PortalProtocolException;

    /**
     * Create PAP authentication packet.
     *
     * @param credentials user credentials.
     * @return PAP authentication packet.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Packet createPapAuthPacket(Credentials credentials)
            throws IOException, PortalProtocolException;

    /**
     * Create logout request packet.
     *
     * @param credentials user credentials.
     * @return logout request packet, or null if ip address in credentials is unknown.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Packet createLogoutPacket(Credentials credentials)
            throws IOException, PortalProtocolException;

    /**
     * Handle CHAP request not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onChapRespond(Packet request)
            throws IOException, PortalProtocolException;

    /**
     * Handle CHAP request not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onChapRequestNotRespond(Packet request)
            throws IOException, PortalProtocolException;

    /**
     * Handle authentication request not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onAuthenticationNotRespond(Packet request)
            throws IOException, PortalProtocolException;

    /**
     * Handle authentication response.
     * @param response response.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onAuthenticationResponse(Packet response)
            throws IOException, PortalProtocolException;

    /**
     * Handle logout response.
     * @param response response.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onLogoutResponse(Packet response)
            throws IOException, PortalProtocolException;

    /**
     * Handle logout not respond.
     * @param request request.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    protected abstract Result onLogoutNotRespond(Packet request)
            throws IOException, PortalProtocolException;

    @Override
    public Result login(Credentials credentials) throws IOException, PortalProtocolException {
        Objects.requireNonNull(credentials);

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
                Result chapResponse = onChapRespond(chapAck);
                if (logger.isTraceEnabled()) {
                    logger.trace("CHAP response: {}", chapResponse);
                }

                request = createChapAuthPacket(chapAck, credentials);
                response = request(request);
                break;

            case PAP:
                request = createPapAuthPacket(credentials);
                response = request(request);
                break;

            default:
                throw new UnsupportedAuthenticationTypeExceptionPortal(authType);
        }

        /* Check authentication response. */
        if (response.isPresent()) {
            logger.debug("Handle authentication response.");
            return onAuthenticationResponse(response.get());
        } else {
            logger.debug("Handle authentication timeout.");
            return onAuthenticationNotRespond(request);
        }
    }

    @Override
    public Result logout(Credentials credentials) throws IOException, PortalProtocolException {
        Objects.requireNonNull(credentials);

        /* Create portal request to logout. */
        Packet logout = createLogoutPacket(credentials);
        Optional<Packet> response = request(logout);

        if (!response.isPresent()) {
            logger.debug("Handle logout timeout.");
            return onLogoutNotRespond(logout);
        } else {
            logger.debug("Handle logout response.");
            return onLogoutResponse(response.get());
        }
    }
}
