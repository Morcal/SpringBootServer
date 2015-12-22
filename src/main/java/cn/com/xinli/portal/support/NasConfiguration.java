package cn.com.xinli.portal.support;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class NasConfiguration {
    private long id;
    private String name;
    private String ipv4Address;
    private String ipv6Address;
    private String type;
    private int listenPort;
    private String authType;
    private String ipv4start;
    private String ipv4end;
    private String sharedSecret;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }

    public void setIpv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpv4end() {
        return ipv4end;
    }

    public void setIpv4end(String ipv4end) {
        this.ipv4end = ipv4end;
    }

    public String getIpv4start() {
        return ipv4start;
    }

    public void setIpv4start(String ipv4start) {
        this.ipv4start = ipv4start;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    public String toString() {
        return "NasConfiguration{" +
                "authType='" + authType + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", ipv4Address='" + ipv4Address + '\'' +
                ", ipv6Address='" + ipv6Address + '\'' +
                ", type='" + type + '\'' +
                ", listenPort=" + listenPort +
                ", ipv4start='" + ipv4start + '\'' +
                ", ipv4end='" + ipv4end + '\'' +
                ", sharedSecret='" + sharedSecret + '\'' +
                '}';
    }
}
