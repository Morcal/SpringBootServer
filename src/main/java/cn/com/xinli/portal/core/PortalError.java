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

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "PortalError{" +
                "code=" + code +
                ", text='" + text + '\'' +
                '}';
    }

    /**
     * Create a portal error.
     * @param code error code.
     * @param text error text.
     * @return portal error.
     */
    public static PortalError of(int code, String text) {
        return new PortalError(code, text);
    }

    /**
     * All defined portal java client errors.
     */
    private static final PortalError[] errors = {
            /* System internal errors. */
            of(1, "server_failed_to_start"),
            of(2, "server_failed_to_listen"),
            of(3, "server_failed_to_bind"),
            of(4, "server_failed_to_load_db"),
            of(5, "invalid_db_schema"),
            of(13, "server_internal_error"),
            of(14, "service_unavailable"),
            of(21, "invalid_pws_configuration"),
            of(31, "invalid_nas_configuration"),
            of(41, "need_ssl"),
            of(61, "redundant_api_registration"),
            of(62, "redundant_api_entry"),

            /* NAS errors. */
            of(70, "nas_unreachable"),
            of(71, "nas_not_respond"),
            of(72, "unsupported_protocol"),
            of(73, "unsupported_nas"),
            of(74, "unsupported_authentication"),
            of(75, "invalid_credentials"),
            of(76, "unrecognized_response"),
            of(77, "nas_not_found"),
            of(81, "authentication_rejected"),
            of(82, "authentication_already_online"),
            of(83, "authentication_unavailable"),
            of(84, "authentication_failure"),
            of(85, "challenge_rejected"),
            of(86, "challenge_already_online"),
            of(87, "challenge_unavailable"),
            of(88, "challenge_failure"),
            of(89, "logout_rejected"),
            of(90, "logout_failure"),
            of(91, "logout_already_gone"),

            /* REST API errors. */
            of(101, "invalid_client"),
            of(102, "invalid_scope"),
            of(103, "invalid_client_grant"),
            of(104, "invalid_update_timestamp"),
            of(105, "challenge_not_found"),
            of(106, "invalid_challenge_response"),
            of(107, "bad_client_credentials"),
            of(108, "invalid_certificate"),
            of(109, "unsupported_response_type"),
            of(110, "rest_authentication_error"),
            of(121, "invalid_session_grant"),
            of(131, "invalid_system_grant"),
            of(141, "invalid_request"),
            of(151, "rest_request_rate_limited"),

            /* Portal service errors. */
            of(201, "invalid_authenticate_credentials"),
            of(202, "invalid_account_type"),
            of(203, "invalid_account_state"),
            of(204, "inactive_account"),
            of(205, "invalid_author_credentials"),
            of(206, "max_session_count"),
            of(207, "not_allowed"),
            of(209, "nat_not_allowed"),

            of(231, "x_invalid_credentials"),
            of(232, "x_inactive_account"),
            of(233, "x_account_already_online"),
            of(234, "x_port_term_not_allowed"),
            of(235, "x_dial_forbidden"),
            of(236, "x_dial_rejected_bad_encode"),
            of(237, "x_term_port_not_allowed"),
            of(238, "x_no_package_available"),

            of(239, "login_too_often"),
            of(240, "already_login_in"),
            of(241, "user_not_found"),
            of(242, "incorrect_password"),
            of(243, "invalid_account_state"),
            of(244, "need_merge_account"),
            of(245, "already_in"),
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
