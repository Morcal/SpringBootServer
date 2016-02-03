package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.credentials.Credentials;

import java.io.IOException;

/**
 * Portal Client.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface PortalClient {
    /**
     * Login.
     * @param credentials user credentials.
     * @return portal message.
     * @throws IOException
     * @throws TransportException
     * @throws NullPointerException if credentials is null.
     */
    Result login(Credentials credentials) throws IOException, TransportException;

    /**
     * Logout.
     * @param credentials user credentials.
     * @return portal message.
     * @throws IOException
     * @throws TransportException
     * @throws NullPointerException if credentials is null.
     */
    Result logout(Credentials credentials) throws IOException, TransportException;
}
