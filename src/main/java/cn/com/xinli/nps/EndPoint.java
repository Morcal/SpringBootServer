package cn.com.xinli.nps;

import java.net.InetSocketAddress;

/**
 * Net policy server end point.
 *
 * This class stores information about a net policy server endpoint.
 *
 * <p>This includes the address of the remote endpoint and the shared secret
 * used for securing the communication.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/1.
 */
public class EndPoint {
    /** Remote address. */
    private InetSocketAddress endpointAddress;

    /** RADIUS shared secret. */
    private String sharedSecret;

    /**
     * Constructs a RadiusEndpoint object.
     * @param remoteAddress remote address (ip and port number)
     * @param sharedSecret shared secret
     */
    public EndPoint(InetSocketAddress remoteAddress, String sharedSecret) {
        this.endpointAddress = remoteAddress;
        this.sharedSecret = sharedSecret;
    }

    /**
     * Returns the remote address.
     * @return remote address
     */
    public InetSocketAddress getEndpointAddress() {
        return endpointAddress;
    }

    /**
     * Returns the shared secret.
     * @return shared secret
     */
    public String getSharedSecret() {
        return sharedSecret;
    }
}
