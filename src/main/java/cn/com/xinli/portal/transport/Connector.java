package cn.com.xinli.portal.transport;

import cn.com.xinli.portal.core.credentials.Credentials;

import java.io.IOException;

/**
 * Portal connector.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface Connector<T extends Context> {
    /**
     * Login.
     * @param credentials user credentials.
     * @return login result as session extended information.
     * @throws IOException
     * @throws TransportException
     * @throws NullPointerException if credentials is null.
     */
    T login(Credentials credentials) throws IOException, TransportException;

    /**
     * Logout.
     * @param credentials user credentials.
     * @param context context.
     * @throws IOException
     * @throws TransportException
     * @throws NullPointerException if credentials is null.
     */
    void logout(Credentials credentials, T context) throws IOException, TransportException;
}
