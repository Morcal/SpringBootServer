package cn.com.xinli.portal.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * PWS Supported Errors.
 * <p>
 * <p>All errors are defined in three logic scopes.
 * <ul>
 * <li>Internal server errors</li>
 * Internal errors have code range of 1~99.
 * <p>
 * <li>REST API errors</li>
 * REST API errors have code range of 100~199
 * <p>
 * <li>Portal service errors</li>
 * Portal server errors describe errors occurred in the whole portal service
 * process, involves portal web server, NAS/BRAS devices and AAA platform.
 * PWS translate all possible abnormal process results into {@link PortalError}s.
 * </ul>
 * Project: xpws
 *
 * @author zhoupeng 2016/1/17.
 */
public class PortalError {
    /**
     * PortalError code.
     */
    private final int code;

    /**
     * PortalError text.
     */
    private final String text;

    public PortalError(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static PortalError of(int code, String text) {
        return new PortalError(code, text);
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    /**
     * All defined portal java client errors.
     */
    private static final PortalError[] errors = {
            /* System internal errors. */
            of(1, "server_error"),
            of(1, "temporarily_unavailable"),
            of(1, "request_rate_limited"),
            of(1, "not_allowed"),
            of(1, "unauthorized_request"),
            of(1, "unknown_system_error"),

            /* REST API errors. */
            of(1, "invalid_client"),
            of(1, "invalid_request"),
            of(1, "invalid_scope"),
            of(1, "invalid_client_grant"),
            of(1, "invalid_session_grant"),
            of(1, "invalid_system_grant"),
            of(1, "invalid_credential"),
            of(1, "invalid_session_operation"),
            of(1, "invalid_update_timestamp"),

            /* Portal service errors. */
            of(231, "1|90|"),
            of(232, "2|91|"),
            of(233, "4|82|"),
            of(234, "8|134|"),
            of(235, "44|-1|"),
            of(236, "-1|-1|"),
            of(237, "8|180|"),
            of(238, "10|53|"),
            of(239, "Login interval is too short"),
            of(240, "You are already logged in - access denied"),
            of(241, "User not found"),
            of(242, "Password Error"),
            of(243, "User state error!Pls.recharge or connect the admin"),
            of(244, "Please the merge account to login"),
            of(245, "user already in"),
            of(249, "xinli_unknown_error"),

            of(291, "unknown_login_error"),
            of(292, "unknown_logout_error"),
            of(299, "unknown_portal_error")
    };

    /**
     * Get portal error by code.
     *
     * @param text error text.
     * @return portal error.
     */
    public static PortalError of(String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("portal error can not be blank.");
        }

        Optional<PortalError> error = Stream.of(errors)
                .filter(text::equals)
                .findFirst();
        error.orElseThrow(() ->
                new IllegalArgumentException("Portal error: " + text + " not exists."));
        return error.get();
    }

    /**
     * Get portal error by code.
     *
     * @param code error code.
     * @return portal error.
     */
    public static PortalError of(int code) {
        Optional<PortalError> error = Stream.of(errors)
                .filter(err -> err.getCode() == code)
                .findFirst();

        error.orElseThrow(() ->
                new IllegalArgumentException("Portal error code: " + code + " not exists."));
        return error.get();
    }
}
