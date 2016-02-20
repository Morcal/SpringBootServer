package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.TransportException;

import java.io.IOException;

/**
 * HUAWEI portal connector handler.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public interface ConnectorHandler {
    /**
     * Handle CHAP request response.
     * @param response response.
     * @throws IOException
     * @throws TransportException
     */
    void handleChapResponse(Packet response) throws IOException, TransportException;

    /**
     * Handle remote server not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @throws IOException
     * @throws TransportException
     */
    void handleServerNotRespond(Endpoint endpoint) throws IOException, TransportException;
    /**
     * Handle authentication response.
     * @param response response.
     * @throws IOException
     * @throws TransportException
     */
    void handleAuthenticationResponse(Packet response) throws IOException, TransportException;

    /**
     * Handle logout response.
     * @param response response.
     * @throws IOException
     */
    void handleLogoutResponse(Packet response) throws IOException, TransportException;
}
