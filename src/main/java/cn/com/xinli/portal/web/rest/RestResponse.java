package cn.com.xinli.portal.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST APIs response.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class RestResponse {

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
     * @param authorization authorization.
     */
    public void setAuthorization(Authorization authorization) {
        this.authorization = authorization;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
                "truncated=" + truncated +
                ", createdAt=" + createdAt +
                ", authorization=" + authorization +
                ", authentication=" + authentication +
                '}';
    }
}
