package cn.com.xinli.portal;

/**
 * PWS server configuration.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/17.
 */
public class PortalServerConfig {
    private int portalServerListenPort;
    private int portalServerThreadSize;
    private String portalServerSharedSecret;

    public int getPortalServerListenPort() {
        return portalServerListenPort;
    }

    public void setPortalServerListenPort(int portalServerListenPort) {
        this.portalServerListenPort = portalServerListenPort;
    }

    public String getPortalServerSharedSecret() {
        return portalServerSharedSecret;
    }

    public void setPortalServerSharedSecret(String portalServerSharedSecret) {
        this.portalServerSharedSecret = portalServerSharedSecret;
    }

    public int getPortalServerThreadSize() {
        return portalServerThreadSize;
    }

    public void setPortalServerThreadSize(int portalServerThreadSize) {
        this.portalServerThreadSize = portalServerThreadSize;
    }
}
