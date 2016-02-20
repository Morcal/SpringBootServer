package cn.com.xinli.portal.core.configuration;

/**
 * REST configuration.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class RestConfiguration {
    private String host;
    private String server;
    private String scheme;
    private String header;
    private String meta;
    private int challengeTtl;
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
