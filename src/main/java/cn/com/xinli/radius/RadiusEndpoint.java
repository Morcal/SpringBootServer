package cn.com.xinli.radius;

import java.net.InetSocketAddress;

/**
 * This class stores information about a Radius endpoint.
 *
 * <p>This includes the address of the remote endpoint and the shared secret
 * used for securing the communication.
 */
public class RadiusEndpoint {
	/** Remote address. */
	private InetSocketAddress endpointAddress;

	/** RADIUS shared secret. */
	private String sharedSecret;

	/**
	 * Constructs a RadiusEndpoint object.
	 * @param remoteAddress remote address (ip and port number)
	 * @param sharedSecret shared secret
	 */
	public RadiusEndpoint(InetSocketAddress remoteAddress, String sharedSecret) {
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
