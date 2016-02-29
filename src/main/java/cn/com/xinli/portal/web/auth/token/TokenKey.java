package cn.com.xinli.portal.web.auth.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Token key.
 *
 * <p>>Project: xpws
 *
 * @author zhoupeng 2016/2/25.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenKey {
    /** Create time in milliseconds. */
    @JsonProperty("creation_time")
    private long creationTime;

    /** Token scope. */
    @JsonProperty
    private TokenScope scope;

    /** Random value. */
    @JsonProperty
    private String random;

    /** Extended information. */
    @JsonProperty("extended_information")
    private String extendedInformation;

    /** Digest. */
    @JsonProperty
    private String digest;

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public TokenScope getScope() {
        return scope;
    }

    public void setScope(TokenScope scope) {
        this.scope = scope;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public String getExtendedInformation() {
        return extendedInformation;
    }

    public void setExtendedInformation(String extendedInformation) {
        this.extendedInformation = extendedInformation;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    /**
     * Get token key content without digest information.
     * @return content in bytes.
     */
    @JsonIgnore
    public String getContent() {
        return scope.name() + creationTime + random + extendedInformation;
    }

    @Override
    public String toString() {
        return "TokenKey{" +
                "creationTime=" + creationTime +
                ", scope=" + scope +
                ", random='" + random + '\'' +
                ", extendedInformation='" + extendedInformation + '\'' +
                ", digest='" + digest + '\'' +
                '}';
    }
}
