package cn.com.xinli.portal.core;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * PWS Supported Errors.
 *
 * <p>All errors are defined in three logic scopes.
 *
 * <ul>
 * <li>Internal server errors</li>
 * Internal errors have value range of 1~99.
 * <li>REST API errors</li>
 * REST API errors have value range of 100~199
 * <li>Portal service errors</li>
 * Portal server errors describe errors occurred in the whole portal service
 * process, involves portal web server, NAS/BRAS devices and AAA platform.
 * PWS translate all possible abnormal process results into {@link PortalError}s.
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/17.
 */
public enum PortalError {
    /* System internal errors. */
    SERVER_FAILED_TO_START(1, "server failed to start"),
    SERVER_FAILED_TO_LISTEN(2, "server failed to listen"),
    SERVER_FAILED_TO_BIND(3, "server failed to bind"),
    SERVER_FAILED_TO_LOAD_DB(4, "server failed to load db"),
    INVALID_DB_SCHEMA(5, "invalid db schema"),
    SERVER_API_UPGRADED(11, "server api upgraded"),
    SERVER_MAINTENANCE(12, "server maintenance"),
    SERVER_INTERNAL_ERROR(13, "server internal error"),
    SERVICE_UNAVAILABLE(14, "service unavailable"),
    IO_ERROR(15, "server io error"),
    ACTIVITY_ACTION_ERROR(16, "activity action error"),
    CACHING_ERROR(17, "caching error"),
    SESSION_PROVIDER_NOT_FOUND(18, "session provider not found"),
    RADIUS_NOT_FOUND(19, "radius server not found"),
    MISSING_PWS_CONFIGURATION(21, "missing configuration pws.properties"),
    PWS_CONFIGURATION_TYPE_ERROR(22, "invalid pws configuration value type"),
    PWS_CONFIGURATION_BLANK(22, "pws configuration is blank"),
    INVALID_NAS_CONFIGURATION(31, "invalid nas configuration"),
    NAS_NOT_FOUND(32, "nas not found"),
    NEED_SSL(41, "need ssl"),
    REDUNDANT_API_REGISTRATION(61, "redundant api registration"),
    REDUNDANT_API_ENTRY(62, "redundant api entry"),

    /* NAS errors. */
    NAS_UNREACHABLE(70, "nas unreachable"),
    NAS_NOT_RESPOND(71, "nas not respond"),
    UNSUPPORTED_PROTOCOL(72, "unsupported protocol"),
    UNSUPPORTED_NAS(73, "unsupported nas"),
    UNSUPPORTED_AUTHENTICATION(74, "unsupported authentication"),
    UNRECOGNIZED_RESPONSE(75, "unrecognized response"),
    AUTHENTICATION_REJECTED(81, "authentication rejected"),
    AUTHENTICATION_ALREADY_ONLINE(82, "authentication already online"),
    AUTHENTICATION_UNAVAILABLE(83, "authentication unavailable"),
    AUTHENTICATION_FAILURE(84, "authentication failure"),
    CHALLENGE_REJECTED(85, "challenge rejected"),
    CHALLENGE_ALREADY_ONLINE(86, "challenge already online"),
    CHALLENGE_UNAVAILABLE(87, "challenge unavailable"),
    CHALLENGE_FAILURE(88, "challenge failure"),
    LOGOUT_REJECTED(89, "logout rejected"),
    LOGOUT_FAILURE(90, "logout failure"),
    LOGOUT_ALREADY_GONE(91, "logout already gone"),
    UNKNOWN_TRANSPORT_ERROR(92, "unknown transport error"),

    /* REST API errors. */
    INVALID_CLIENT(101, "invalid client"),
    INVALID_CERTIFICATE(102, "invalid certificate"),
    INVALID_SCOPE(103, "invalid scope"),
    INVALID_CLIENT_GRANT(104, "invalid client grant"),
    CHALLENGE_NOT_FOUND(105, "challenge not found"),
    INVALID_CHALLENGE_RESPONSE(106, "invalid challenge response"),
    BAD_CLIENT_CREDENTIALS(107, "bad client credentials"),
    UNSUPPORTED_RESPONSE_TYPE(108, "unsupported response type"),
    REST_AUTHENTICATION_ERROR(109, "rest authentication error"),
    UNAUTHORIZED_REQUEST(110, "unauthorized request"),
    INVALID_SESSION_GRANT(121, "invalid session grant"),
    SESSION_NOT_FOUND(122, "session not found"),
    INVALID_UPDATE_TIMESTAMP(123, "invalid update timestamp"),
    INVALID_SYSTEM_GRANT(131, "invalid system grant"),
    INVALID_CREDENTIALS(132, "invalid credentials"),
    INVALID_REQUEST(142, "invalid request"),
    UNPROCESSABLE_ENTITY(143, "unprocessable entity"),
    REST_REQUEST_RATE_LIMITED(151, "rest request rate limited"),
    INVALID_ENVIRONMENT(152, "not in a portal environment"),

    /* Portal service errors. */
    INVALID_AUTHENTICATE_CREDENTIALS(201, "invalid authenticate credentials"),
    INVALID_ACCOUNT_TYPE(202, "invalid account type"),
    INVALID_ACCOUNT_STATE(203, "invalid account state"),
    INACTIVE_ACCOUNT(204, "inactive account"),
    INVALID_AUTHOR_CREDENTIALS(205, "invalid author credentials"),
    MAX_SESSION_COUNT(206, "max session count"),
    NOT_ALLOWED(207, "not allowed"),
    PORT_NOT_ALLOWED(208, "port not allowed"),
    NAT_NOT_ALLOWED(209, "nat not allowed"),
    NOT_ENOUGH_BALANCE(210, "not enough balance"),
    TIME_QUOTA_EXCEEDED(211, "time quota exceeded"),
    QUOTA_EXCEEDED(212, "quota exceeded"),
    X_INVALID_CREDENTIALS(231, "x invalid credentials"),
    X_INACTIVE_ACCOUNT(232, "x inactive account"),
    X_ACCOUNT_ALREADY_ONLINE(233, "x account already online"),
    X_PORT_TERM_NOT_ALLOWED(234, "x port term not allowed"),
    X_DIAL_FORBIDDEN(235, "x dial forbidden"),
    X_DIAL_REJECTED_BAD_ENCODE(236, "x dial rejected bad encode"),
    X_TERM_PORT_NOT_ALLOWED(237, "x term port not allowed"),
    X_NO_PACKAGE_AVAILABLE(238, "x no package available"),
    XL_LOGIN_TOO_OFTEN(239, "xl login too often"),
    XL_ALREADY_LOGIN_IN(240, "xl already login in"),
    XL_USER_NOT_FOUND(241, "xl user not found"),
    XL_INCORRECT_PASSWORD(242, "xl incorrect password"),
    XL_INVALID_ACCOUNT_STATE(243, "xl invalid account state"),
    XL_NEED_MERGE_ACCOUNT(244, "xl need merge account"),
    XL_ALREADY_IN(245, "xl already in"),
    XL_UNKNOWN_ERROR(249, "xl unknown error"),
    NETWORK_CHANGED(250, "session exists, network changed"),
    UNKNOWN_LOGIN_ERROR(291, "unknown login error"),
    UNKNOWN_LOGOUT_ERROR(292, "unknown logout error"),
    UNKNOWN_PORTAL_ERROR(299, "unknown portal error");

    public static final String EMPTY_ERROR = "Portal error is empty.";

    /** PortalError value. */
    private final int value;

    /** PortalError reason. */
    private final String reason;

    PortalError(int value, String reason) {
        this.value = value;
        this.reason = reason;
    }

    /**
     * Get portal error value.
     * @return portal error value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Get error reason.
     * @return error reason.
     */
    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "PortalError{" +
                "value=" + value +
                ", reason='" + reason + '\'' +
                '}';
    }

    /**
     * Check if error is a system error.
     * @return true if error is a system error.
     */
    public boolean isSystemError() {
        return Series.SYSTEM == Series.of(value);
    }

    /**
     * Check if error is a rest error.
     * @return true if error is a rest error.
     */
    public boolean isRestError() {
        return Series.REST_SERVICE == Series.of(value);
    }

    /**
     * Check if error is a service error.
     * @return true if error is a service error.
     */
    public boolean isServiceError() {
        return Series.PORTAL_SERVICE == Series.of(value);
    }

    /**
     * Get portal error by value.
     *
     * @param value error value.
     * @return portal error.
     */
    public static PortalError of(int value) {
        Optional<PortalError> error = Stream.of(values())
                .filter(err -> err.getValue() == value)
                .findFirst();

        error.orElseThrow(() ->
                new IllegalArgumentException("Portal error value: " + value + " not exists."));
        return error.get();
    }

    /** Portal error series. */
    public enum Series {
        SYSTEM(1),
        REST_SERVICE(2),
        PORTAL_SERVICE(3);

        private final int value;

        Series(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        /**
         * Get series of error.
         * @param value error.
         * @return series.
         */
        public static Series of(int value) {
            int v = value / 100 + 1;
            Optional<Series> series = Stream.of(values())
                    .filter(s -> s.value == v)
                    .findAny();

            series.orElseThrow(() ->
                    new IllegalArgumentException("no mathching constant for: " + value));

            return series.get();
        }
    }
}
