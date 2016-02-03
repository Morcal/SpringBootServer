package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.Result;

import java.io.IOException;

/**
 * HUAWEI portal client handler.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public interface ClientHandler<T> {
    /**
     * Handle CHAP request response.
     * @param response response.
     * @return message.
     * @throws IOException
     * @throws TransportException
     */
    Result handleChapResponse(T response) throws IOException, TransportException;

    /**
     * Handle CHAP request not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws TransportException
     */
    Result handleChapNotRespond(Endpoint endpoint) throws IOException, TransportException;

    /**
     * Handle authentication request not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws TransportException
     */
    Result handleAuthenticationNotRespond(Endpoint endpoint) throws IOException, TransportException;

    /**
     * Handle authentication response.
     * @param response response.
     * @return message.
     * @throws IOException
     * @throws TransportException
     */
    Result handleAuthenticationResponse(T response) throws IOException, TransportException;

    /**
     * Handle logout response.
     * @param response response.
     * @return message.
     * @throws IOException
     */
    Result handleLogoutResponse(T response) throws IOException, TransportException;

    /**
     * Handle logout not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws TransportException
     */
    Result handleLogoutNotRespond(Endpoint endpoint) throws IOException, TransportException;
}
