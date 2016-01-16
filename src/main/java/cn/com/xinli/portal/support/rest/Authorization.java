package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Authorization.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/13.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authorization {
    /** Token string. */
    private String token;

    /** Token type. */
    @JsonProperty("token_type")
    private String tokenType;

    /** UNIX epoch time token expires at. */
    @JsonProperty("expires_at")
    private long expiresAt;

    /** Related refresh token. */
    @JsonProperty("refresh_token")
    private String refreshToken;

    /** Token expires in seconds. */
    @JsonProperty("expires_in")
    private long expiresIn;

    /** Authorization scope. */
    private String scope;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Authorization{" +
                "token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", expiresAt=" + expiresAt +
                ", refreshToken='" + refreshToken + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
