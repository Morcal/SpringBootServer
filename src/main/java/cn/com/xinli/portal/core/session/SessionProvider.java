package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;

/**
 * Session Provider.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public interface SessionProvider {

    /**
     * Create portal session on remote {@link Nas} using given {@link Credentials}.
     *
     * <p>Given session is normally filled by default, credentials in that session
     * is also the default. Implementation classes
     * should return a session with full populated credentials associated with
     * nas type. For example, a provider implements HUAWEI portal session service
     * provider should return a populated session.
     *
     * @param nas NAS/BRAS device.
     * @param credentials user credentials.
     * @return full populated session.
     * @throws PortalException
     */
    Session authenticate(Nas nas, Credentials credentials) throws PortalException;

    /**
     * Disconnect an existed portal connection.
     * @param session session to disconnect.
     * @throws PortalException
     */
    void disconnect(Session session) throws PortalException;

    /**
     * Check if this provider supports given nas.
     * @param nas NAS/BRAS device.
     * @return true if this provider supports given nas.
     */
    boolean supports(Nas nas);
}
