package cn.com.xinli.portal.support;

import cn.com.xinli.nps.NetPolicyServer;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.RadiusNas;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Session authenticate(Nas nas, Credentials credentials) throws PortalException {
        //TODO implement authenticate.
        return null;
    }

    @Override
    public void disconnect(Session session) throws PortalException {
        //TODO implement disconnect
    }

    @Override
    public boolean supports(Nas nas) {
        return nas != null && RadiusNas.class.isInstance(nas);
    }
}
