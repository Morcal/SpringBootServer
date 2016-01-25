package cn.com.xinli.portal.transport;

import java.io.IOException;

/**
 * Portal server.
 *
 * <p>Portal server listens on local port, receives incoming requests encoded
 * in portal protocol, and serves those requests and responds back to remote
 * client.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public interface PortalServer {
    /**
     * Start portal server.
     * @throws IOException
     */
    void start() throws IOException;

    /**
     * Shutdown portal server.
     */
    void shutdown();
}
