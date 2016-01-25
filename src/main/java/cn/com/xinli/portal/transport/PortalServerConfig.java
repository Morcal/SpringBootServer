package cn.com.xinli.portal.transport;

/**
 * PWS server configuration.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public class PortalServerConfig {
    /** UDP datagram server listen port. */
    private int listenPort;

    /** Core worker thread size. */
    private int threadSize;

    /** Shared secret. */
    private String sharedSecret;

    public int getListenPort() {
        return listenPort;
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public String getSharedSecret() {
        return sharedSecret;
    }

    public void setSharedSecret(String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    public int getThreadSize() {
        return threadSize;
    }

    public void setThreadSize(int threadSize) {
        this.threadSize = threadSize;
    }
}
