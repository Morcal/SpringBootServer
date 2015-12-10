package cn.com.xinli.portal.rest;

/**
 * Portal REST APIs response.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/9.
 */
public abstract class RestResponse {
    public static final String ERROR_INVALID_CLIENT = "invalid_client";
    public static final String ERROR_INVALID_REQUEST = "invalid_request";
    public static final String ERROR_INVALID_SCOPE = "invalid_scope";
    public static final String ERROR_SERVER_ERROR = "server_error";
    public static final String ERROR_TEMPORIRILY_UNAVAILABLE = "temporarily_unavailable";
    public static final String ERROR_INVALID_CREDENTIAL = "invalid_credential";
    public static final String ERROR_INVALID_PORTAL_REQUEST = "invalid_portal_request";
    public static final String ERROR_INVALID_ACCOUNT_TYPE = "invalid_account_type";
    public static final String ERROR_INVALID_ACCOUNT_STATE = "invalid_account_state";
    public static final String ERROR_ACCOUNT_INACTIVE = "account_inactive";
    public static final String ERROR_MAX_SESSION_COUNT = "max_session_count";
    public static final String ERROR_PORT_NOT_ALLOWED = "port_not_allowed";
    public static final String ERROR_REQUEST_RATE_LIMITED = "request_rate_limited";
    public static final String ERROR_NOT_ALLOWED = "not_allowed";
    public static final String ERROR_UNKNOWN_ERROR = "unknown_error";

    /**
     * REST Error Response.
     */
    public static class Error {
        /** Error code. */
        private final String error;

        /** Error description. */
        private final String error_description;

        /** Associated URL. */
        private final String error_url;

        Error(String error, String error_description, String error_url) {
            this.error = error;
            this.error_description = error_description;
            this.error_url = error_url;
        }

        public String getError() {
            return error;
        }

        public String getError_description() {
            return error_description;
        }

        public String getError_url() {
            return error_url;
        }
    }

}
