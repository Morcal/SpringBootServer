package cn.com.xinli.portal.transport;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * HUAWEI Portal Protocol Error.
 *
 * <p>This class defines errors in the communication between portal-client
 * and NAS/BRAS.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/18.
 */
public enum ProtocolError {
    NAS_UNREACHABLE(0xa00, "NAS unreachable"),
    NAS_NOT_RESPOND(0xa01, "NAS not respond"),
    UNSUPPORTED_PROTOCOL(0xa02, "unsupported protocol"),
    UNSUPPORTED_NAS(0xa03, "unsupported NAS"),
    UNSUPPORTED_AUTHENTICATION(0xa04, "unsupported authentication"),
    UNRECOGNIZED_RESPONSE(0xa05, "unrecognized response"),
    INVALID_CREDENTIALS(0xa06, "invalid credentials"),
    AUTHENTICATION_REJECTED(0xa0b, "authentication rejected"),
    AUTHENTICATION_ALREADY_ONLINE(0xa0c, "authentication already online"),
    AUTHENTICATION_UNAVAILABLE(0xa0d, "authentication unavailable"),
    AUTHENTICATION_FAILURE(0xa0e, "authentication failure"),
    CHALLENGE_REJECTED(0xa0f, "challenge rejected"),
    CHALLENGE_ALREADY_ONLINE(0xa10, "challenge already online"),
    CHALLENGE_UNAVAILABLE(0xa11, "challenge unavailable"),
    CHALLENGE_FAILURE(0xa12, "challenge failure"),
    LOGOUT_REJECTED(0xa13, "logout rejected"),
    LOGOUT_FAILURE(0xa14, "logout failure"),
    LOGOUT_ALREADY_GONE(0xa15, "logout already gone");

    /** protocol error value. */
    private final int value;

    /** protocol error reason. */
    private final String reason;

    ProtocolError(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    public int getValue() {
        return value;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ProtocolError{" +
                "value=" + value +
                ", reason='" + reason + '\'' +
                '}';
    }

    /**
     * Get portal error by value.
     * @param value error reason.
     * @return portal error.
     */
    public static ProtocolError of(int value) {
        Optional<ProtocolError> error = Stream.of(values())
                .filter(err -> err.value == value)
                .findFirst();

        error.orElseThrow(() -> new IllegalArgumentException("error: " + value + " not exists."));
        return error.get();
    }

    /**
     * Check if error is a challenge error.
     * @return true if error is a challenge error.
     */
    public boolean isChallengeError() {
        return value >= CHALLENGE_REJECTED.value && value <= CHALLENGE_FAILURE.value;
    }

    /**
     * Check if error is a challenge error.
     * @return true if error is a challenge error.
     */
    public boolean isAuthenticationError() {
        return value >= AUTHENTICATION_REJECTED.value && value <= AUTHENTICATION_FAILURE.value;
    }

    /**
     * Check if error is a challenge error.
     * @return true if error is a challenge error.
     */
    public boolean isLogoutError() {
        return value >= LOGOUT_REJECTED.value && value <= LOGOUT_ALREADY_GONE.value;
    }
}
