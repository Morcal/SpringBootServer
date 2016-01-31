package cn.com.xinli.portal.core.session;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;

import java.net.UnknownHostException;

/**
 * Session Provider.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
public interface SessionProvider {
    Session createSession(Nas nas, Credentials credentials) throws UnknownHostException;
    Session authenticate(Session session) throws PortalException;
    Session hangup(Session session) throws PortalException;
    boolean supports(Nas nas);
}
