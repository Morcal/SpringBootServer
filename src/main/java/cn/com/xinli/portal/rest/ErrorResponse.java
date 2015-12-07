package cn.com.xinli.portal.rest;

import org.apache.commons.lang3.StringUtils;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public class ErrorResponse {

    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String SERVER_ERROR = "server_error";
    public static final String TEMPORIRILY_UNAVAILABLE = "temporirily_unavailable";
    public static final String INVALID_CREDENTIAL = "invalid_credential";
    public static final String INVALID_PORTAL_REQUEST = "invalid_portal_request";
    public static final String INVALID_ACCOUNT_TYPE = "invalid_account_type";
    public static final String INVALID_ACCOUNT_STATE = "invalid_account_state";
    public static final String ACCOUNT_INACTIVE = "account_inactive";
    public static final String MAX_SESSION_COUNT = "max_session_count";
    public static final String PORT_NOT_ALLOWED = "port_not_allowed";
    public static final String REQUEST_RATE_LIMITED = "request_rate_limited";
    public static final String NOT_ALLOWED = "not_allowed";
    public static final String UNKNOWN_ERROR = "unknown_error";

    private final String error;
    private final String error_description;
    private final String error_url;

    public static ErrorResponseBuilder newBuilder() {
        return new ErrorResponseBuilder();
    }

    static class ErrorResponseBuilder {

        private String error;
        private String description;
        private String url;

        public ErrorResponseBuilder setError(String error) {
            this.error = error;
            return this;
        }

        public ErrorResponseBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public ErrorResponseBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(
                    StringUtils.defaultString(error, ErrorResponse.UNKNOWN_ERROR),
                    StringUtils.defaultString(description),
                    StringUtils.defaultString(url));
        }
    }

    private ErrorResponse(String error, String error_description, String error_url) {
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
