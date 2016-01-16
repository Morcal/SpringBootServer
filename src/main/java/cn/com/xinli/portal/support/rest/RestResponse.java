package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST APIs response.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public abstract class RestResponse {
    public static final String ERROR_INVALID_CLIENT = "invalid_client";
    public static final String ERROR_INVALID_REQUEST = "invalid_request";
    public static final String ERROR_INVALID_SCOPE = "invalid_scope";
    public static final String ERROR_SERVER_ERROR = "server_error";
    public static final String ERROR_INVALID_CLIENT_GRANT = "invalid_client_grant";
    public static final String ERROR_INVALID_SESSION_GRANT = "invalid_session_grant";
    public static final String ERROR_INVALID_SYSTEM_GRANT = "invalid_system_grant";
    public static final String ERROR_TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";
    public static final String ERROR_INVALID_CREDENTIAL = "invalid_credential";
    public static final String ERROR_REQUEST_RATE_LIMITED = "request_rate_limited";
    public static final String ERROR_NOT_ALLOWED = "not_allowed";
    public static final String ERROR_UNAUTHORIZED_REQUEST = "unauthorized_request";
    public static final String ERROR_UNKNOWN_ERROR = "unknown_error";

    /** If response been truncated. */
    @JsonProperty
    private boolean truncated;

    /** server time (UNIX epoch time) when this response was created. */
    @JsonProperty("created_at")
    private long createdAt;

    @JsonProperty
    private Authorization authorization;

    @JsonProperty
    private Authentication authentication;

    /**
     * Set response created time (UNIX epoch time).
     * @param createdAt response created time (UNIX epoch time).
     */
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Set truncated.
     * @param truncated if response been truncated.
     */
    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    /**
     * If response been truncated.
     * @return true if server truncated response.
     */
    public boolean truncated() {
        return this.truncated;
    }

    /**
     * Get server time (UNIX epoch time) when this response
     * was created.
     * @return server time.
     */
    public long createdAt() {
        return this.createdAt;
    }

    /**
     * Get authentication.
     * @return authentication.
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * Set authentication
     * @param authentication authentication.
     */
    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    /**
     * Get authorization.
     * @return authorization.
     */
    public Authorization getAuthorization() {
        return authorization;
    }

    /**
     * Set authorization.
     * @return authorization.
     */
    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }
}
