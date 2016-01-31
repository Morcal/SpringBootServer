package cn.com.xinli.portal.core.configuration;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public class PortalServerConfiguration {
    private String name;
    private String host;
    private int port;
    private String sharedSecret;
    private String version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "PortalServerConfiguration{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
