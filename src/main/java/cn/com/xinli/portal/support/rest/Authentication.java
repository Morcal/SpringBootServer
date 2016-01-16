package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST Authentication response.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Authentication {
    /** Authentication nonce. */
    private String nonce;

    /** Challenge. */
    private String challenge;

    /** UNIX epoch time Challenge expires at. */
    @JsonProperty("expires_at")
    private long expiresAt;

    /** Seconds challenge expires in. */
    @JsonProperty("expires_in")
    private long expiresIn;

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "Authentication{" +
                "nonce='" + nonce + '\'' +
                ", challenge='" + challenge + '\'' +
                ", expiresIn='" + expiresIn + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }
}
