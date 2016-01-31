package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.PortalProtocolException;
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
     * @throws PortalProtocolException
     */
    Result handleChapResponse(T response) throws IOException, PortalProtocolException;

    /**
     * Handle CHAP request not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    Result handleChapNotRespond(Endpoint endpoint) throws IOException, PortalProtocolException;

    /**
     * Handle authentication request not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    Result handleAuthenticationNotRespond(Endpoint endpoint) throws IOException, PortalProtocolException;

    /**
     * Handle authentication response.
     * @param response response.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    Result handleAuthenticationResponse(T response) throws IOException, PortalProtocolException;

    /**
     * Handle logout response.
     * @param response response.
     * @return message.
     * @throws IOException
     */
    Result handleLogoutResponse(T response) throws IOException, PortalProtocolException;

    /**
     * Handle logout not respond.
     * @param endpoint HUAWEI portal endpoint.
     * @return message.
     * @throws IOException
     * @throws PortalProtocolException
     */
    Result handleLogoutNotRespond(Endpoint endpoint) throws IOException, PortalProtocolException;
}
