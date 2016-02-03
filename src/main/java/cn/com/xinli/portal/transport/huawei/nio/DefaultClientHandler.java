package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.transport.*;
import cn.com.xinli.portal.transport.huawei.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Default huawei portal client handler.
 *
 * <p>This handler wraps remote portal endpoint error responses
 * to {@link TransportError}s and throw {@link TransportException}s
 * with those errors.
 *
 * <p>This handler also generates {@link TransportError}s when
 * remote portal endpoint does not respond to requests, and throw
 * {@link TransportException} with those errors.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
final class DefaultClientHandler implements ClientHandler<HuaweiPacket> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(DefaultClientHandler.class);

    @Override
    public Result handleChapNotRespond(Endpoint endpoint)
            throws IOException, TransportException {
        logger.warn("{}", TransportError.NAS_NOT_RESPOND.name());
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleAuthenticationNotRespond(Endpoint endpoint)
            throws IOException, TransportException {
        logger.warn("{}", TransportError.NAS_NOT_RESPOND.name());
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleLogoutNotRespond(Endpoint endpoint)
            throws IOException, TransportException {
        logger.warn("{}", TransportError.NAS_NOT_RESPOND.name());
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleChapResponse(HuaweiPacket response)
            throws IOException, TransportException {
        logger.info("{}", RequestType.ACK_CHALLENGE.name());

        if (logger.isTraceEnabled()) {
            logger.trace("CHAP response {}", response);
        }

        Optional<ChallengeError> err = ChallengeError.valueOf(response.getError());

        err.orElseThrow(() ->
                new UnrecognizedResponseException(
                        "CHAP response error code: " + response.getError()));

        TransportError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = TransportError.CHALLENGE_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = TransportError.CHALLENGE_ALREADY_ONLINE;
                break;

            case WAIT:
                error = TransportError.CHALLENGE_UNAVAILABLE;
                break;

            case FAILED:
                error = TransportError.CHALLENGE_FAILURE;
                break;
        }

        logger.info("{}", error.getReason());
        throw new ChallengeException(error, error.getReason());
    }

    @Override
    public Result handleAuthenticationResponse(HuaweiPacket response)
            throws IOException, TransportException {
        logger.info("{}", RequestType.ACK_AUTH.name());

        if (logger.isTraceEnabled()) {
            logger.trace("AUTH response {}", response);
        }

        Optional<AuthError> err = AuthError.valueOf(response.getError());
        err.orElseThrow(() ->
                new UnrecognizedResponseException("authentication error code:" + response.getError()));

        TransportError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = TransportError.AUTHENTICATION_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = TransportError.AUTHENTICATION_ALREADY_ONLINE;
                break;

            case WAIT:
                error = TransportError.AUTHENTICATION_UNAVAILABLE;
                break;

            case FAILED:
                error = TransportError.AUTHENTICATION_FAILURE;
                break;
        }

        logger.info("{}", error.getReason());
        throw new AuthenticationException(error, Packets.buildText(response));
    }

    @Override
    public Result handleLogoutResponse(HuaweiPacket response)
            throws IOException, TransportException {
        logger.info("{}", RequestType.ACK_LOGOUT.name());

        if (logger.isTraceEnabled()) {
            logger.trace("LOGOUT response {}", response);
        }

        Optional<LogoutError> err = LogoutError.valueOf(response.getError());
        err.orElseThrow(() ->
                new UnrecognizedResponseException("logout error: " + response.getError() + "."));

        TransportError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = TransportError.LOGOUT_REJECTED;
                break;

            case FAILED:
                error = TransportError.LOGOUT_FAILURE;
                break;

            case GONE:
                error = TransportError.LOGOUT_ALREADY_GONE;
                break;
        }

        logger.info("{}", error.getReason());
        throw new LogoutException(error, error.getReason());
    }
}
