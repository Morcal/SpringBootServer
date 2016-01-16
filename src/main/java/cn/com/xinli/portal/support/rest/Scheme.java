package cn.com.xinli.portal.support.rest;

/**
 * REST web service scheme.
 *
 * Project: rest-api
 *
 * @author zhoupeng 2015/12/16.
 */
public class Scheme {
    /** Scheme HTTP header name. */
    private String header;

    /** Scheme meta name. */
    private String meta;

    /** uri scheme, i.e. http/https. */
    private String scheme;

    /** Host name. */
    private String host;

    /** Service port. */
    private int port;

    /** Scheme version. */
    private String version;

    /** Server name. */
    private String server;

    /** Scheme uri. */
    private String uri;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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

    @Override
    public String toString() {
        return "Scheme{" +
                "header='" + header + '\'' +
                ", meta='" + meta + '\'' +
                ", scheme='" + scheme + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", version='" + version + '\'' +
                ", server='" + server + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
