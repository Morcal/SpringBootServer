package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * REST configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestConfiguration {
    /** REST API host. */
    @JsonProperty
    private String host;

    /** REST API server. */
    @JsonProperty
    private String server;

    /** REST API scheme. */
    @JsonProperty
    private String scheme;

    /** REST API header in page http header. */
    @JsonProperty
    private String header;

    /** REST API meta in page meta element. */
    @JsonProperty
    private String meta;

    /** REST API challenge time to live in seconds. */
    @JsonProperty("challenge_ttl")
    private int challengeTtl;

    /** REST Access token time to live in seconds. */
    @JsonProperty("token_ttl")
    private int tokenTtl;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public int getChallengeTtl() {
        return challengeTtl;
    }

    public void setChallengeTtl(int challengeTtl) {
        this.challengeTtl = challengeTtl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getTokenTtl() {
        return tokenTtl;
    }

    public void setTokenTtl(int tokenTtl) {
        this.tokenTtl = tokenTtl;
    }

    @Override
    public String toString() {
        return "RestConfiguration{" +
                "host='" + host + '\'' +
                ", server='" + server + '\'' +
                ", scheme='" + scheme + '\'' +
                ", header='" + header + '\'' +
                ", meta='" + meta + '\'' +
                ", challengeTtl=" + challengeTtl +
                ", tokenTtl=" + tokenTtl +
                '}';
    }
}
