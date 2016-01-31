package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.RadiusNas;
import cn.com.xinli.nps.NetPolicyServer;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;

/**
 * Inter process NPS based session provider.
 *
 * <p>This provider create/delete session from within inter process
 * {@link NetPolicyServer}, no transportation needed.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
@Component
public class InterProcessNpsSessionProvider implements SessionProvider {
    @Autowired
    private NetPolicyServer netPolicyServer;

    @Override
    public Session createSession(Nas nas, Credentials credentials) throws UnknownHostException {
        return null;
    }

    @Override
    public Session authenticate(Session session) throws PortalException {
        return null;
    }

    @Override
    public Session hangup(Session session) throws PortalException {
        return null;
    }

    @Override
    public boolean supports(Nas nas) {
        return nas != null && RadiusNas.class.isInstance(nas);
    }
}
