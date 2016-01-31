package cn.com.xinli.portal.transport.huawei;

import java.net.InetAddress;

/**
 * HUAWEI portal protocol endpoint.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public class Endpoint {
    /** Default listen port. */
    public static final int DEFAULT_LISTEN_PORT = 2000;

    /** Network address. */
    private InetAddress address;

    /** Portal server shared secret. */
    private String sharedSecret;

    /** Listen port. */
    private int port;

    /** Authentication type. */
    private AuthType authType;

    /** HUAWEI portal protocol version. */
    private Version version;

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public AuthType getAuthType() {
        return authType;
    }

    public void setAuthType(AuthType authType) {
        this.authType = authType;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Endpoint{" +
                "address=" + address +
                ", sharedSecret='" + sharedSecret + '\'' +
                ", port=" + port +
                ", authType=" + authType +
                ", version=" + version +
                '}';
    }
}
