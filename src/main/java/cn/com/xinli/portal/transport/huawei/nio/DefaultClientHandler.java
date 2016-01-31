package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.transport.*;
import cn.com.xinli.portal.transport.huawei.*;

import java.io.IOException;
import java.util.Optional;

/**
 * Default huawei portal client handler.
 *
 * <p>This handler wraps remote portal endpoint error responses
 * to {@link ProtocolError}s and throw {@link PortalProtocolException}s
 * with those errors.
 *
 * <p>This handler also generates {@link ProtocolError}s when
 * remote portal endpoint does not respond to requests, and throw
 * {@link PortalProtocolException} with those errors.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
final class DefaultClientHandler implements ClientHandler<HuaweiPacket> {

    @Override
    public Result handleChapNotRespond(Endpoint endpoint)
            throws IOException, PortalProtocolException {
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleAuthenticationNotRespond(Endpoint endpoint)
            throws IOException, PortalProtocolException {
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleLogoutNotRespond(Endpoint endpoint)
            throws IOException, PortalProtocolException {
        throw new NasNotRespondException(endpoint.toString());
    }

    @Override
    public Result handleChapResponse(HuaweiPacket response)
            throws IOException, PortalProtocolException {
        Optional<ChallengeError> err = ChallengeError.valueOf(response.getError());

        err.orElseThrow(() ->
                new UnrecognizedResponseException(
                        "CHAP response error code: " + response.getError()));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = ProtocolError.CHALLENGE_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = ProtocolError.CHALLENGE_ALREADY_ONLINE;
                break;

            case WAIT:
                error = ProtocolError.CHALLENGE_UNAVAILABLE;
                break;

            case FAILED:
                error = ProtocolError.CHALLENGE_FAILURE;
                break;
        }

        throw new ChallengeException(error, error.getReason());
    }

    @Override
    public Result handleAuthenticationResponse(HuaweiPacket response)
            throws IOException, PortalProtocolException {

        Optional<AuthError> err = AuthError.valueOf(response.getError());
        err.orElseThrow(() ->
                new UnrecognizedResponseException("authentication error code:" + response.getError()));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = ProtocolError.AUTHENTICATION_REJECTED;
                break;

            case ALREADY_ONLINE:
                error = ProtocolError.AUTHENTICATION_ALREADY_ONLINE;
                break;

            case WAIT:
                error = ProtocolError.AUTHENTICATION_UNAVAILABLE;
                break;

            case FAILED:
                error = ProtocolError.AUTHENTICATION_FAILURE;
                break;
        }

        throw new AuthenticationException(error, Packets.buildText(response));
    }

    @Override
    public Result handleLogoutResponse(HuaweiPacket response)
            throws IOException, PortalProtocolException {
        Optional<LogoutError> err = LogoutError.valueOf(response.getError());
        err.orElseThrow(() ->
                new UnrecognizedResponseException("logout error: " + response.getError() + "."));

        ProtocolError error = null;
        switch (err.get()) {
            case OK:
                return PortalResult.from(response);

            case REJECTED:
                error = ProtocolError.LOGOUT_REJECTED;
                break;

            case FAILED:
                error = ProtocolError.LOGOUT_FAILURE;
                break;

            case GONE:
                error = ProtocolError.LOGOUT_ALREADY_GONE;
                break;
        }
        throw new LogoutException(error, error.getReason());
    }
}
