package cn.com.xinli.portal.rest.api.v1;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
public class ChallengeImpl implements Challenge {
    private String nonce;
    private String clientId;
    private String challenge;
    private String response;
    private boolean expired;
    private long expiresAt;
    private long createdAt;
    private boolean locked;
    private boolean revoked;

    public ChallengeImpl() {
        createdAt = System.currentTimeMillis();
        locked = false;
        revoked = false;
        expiresAt = Long.MAX_VALUE;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getChallenge() {
        return challenge;
    }

    @Override
    public String getResponse() {
        return response;
    }

    @Override
    public boolean isExpired() {
        return expired;
    }

    @Override
    public boolean isRevoked() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public String getNonce() {
        return nonce;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
