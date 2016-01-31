package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PlatformException;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.transport.PortalClient;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.ProtocolError;
import cn.com.xinli.portal.transport.Result;
import cn.com.xinli.portal.core.nas.HuaweiNas;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.HuaweiSession;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * Huawei portal protocol session provider.
 *
 * <p>This implement is based on a fact that HUAWEI portal protocol logout requests
 * only require user's ip address. So, when portal web server try to logout
 * certain user, user's authentication information includes account name and user's
 * password may be left out. As result, logout user's credentials may can not pass
 * credentials integrity validation.
 *
 * <p>The {@link Session} class was designed to support HUAWEI based portal protocol,
 * and it does not contains full user's credentials. If protocol (such as other protocol
 * providers) requires full user's credentials, then full user's credentials need
 * be accessible through {@link Session}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/29.
 */
@Component
public class HuaweiPortalSessionProvider implements SessionProvider {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiPortalSessionProvider.class);

    @Autowired
    private PortalErrorTranslator errorTranslator;

    @Override
    public boolean supports(Nas nas) {
        return nas != null && HuaweiNas.class.isInstance(nas);
    }

    @Override
    public Session createSession(Nas nas, Credentials credentials) throws UnknownHostException {
        HuaweiNas huaweiNas = HuaweiNas.class.cast(nas);
        HuaweiSession session = new HuaweiSession();
        Endpoint endpoint = new Endpoint();
        endpoint.setVersion(Version.valueOf(huaweiNas.getVersion()));
        endpoint.setAddress(huaweiNas.getNetworkAddress());
        endpoint.setSharedSecret(huaweiNas.getSharedSecret());
        endpoint.setPort(huaweiNas.getListenPort());
        endpoint.setAuthType(AuthType.valueOf(huaweiNas.getAuthType()));

        //FIXME complete session content.
        session.setCredentials(credentials);
        session.setEndpoint(endpoint);
        return session;
    }

    @Override
    public Session authenticate(Session session) throws PortalException {
        HuaweiSession huaweiSession = HuaweiSession.class.cast(session);
        try {
            PortalClient client = HuaweiPortal.createClient(huaweiSession.getEndpoint());
            Result result = client.login(session.getCredentials());

            if (logger.isDebugEnabled()) {
                logger.debug("Portal login result: {}", result);
            }
        } catch (IOException e) {
            logger.error("Portal login error", e);
            throw new ServerException(PortalError.IO_ERROR, "Failed to login", e);
        } catch (PortalProtocolException e) {
            /*
             * Wrap protocol exception into a new platform exception,
             * unless, 1. login CHAP-challenge when already online,
             * 2. login CHAP-authenticate when already online.
             */
            ProtocolError error = e.getProtocolError();
            if (error != ProtocolError.CHALLENGE_ALREADY_ONLINE &&
                    error != ProtocolError.AUTHENTICATION_ALREADY_ONLINE) {
                PortalError err = errorTranslator.translate(e);
                throw new PlatformException(err, e.getMessage(), e);
            }
        }

        return session;
    }

    @Override
    public Session hangup(Session session) throws PortalException {
        HuaweiSession huaweiSession = HuaweiSession.class.cast(session);
        try {
            PortalClient client = HuaweiPortal.createClient(huaweiSession.getEndpoint());
            Result result = client.logout(session.getCredentials());

            if (logger.isDebugEnabled()) {
                logger.debug("Portal logout result: {}", result);
            }
        } catch (IOException e) {
            logger.error("Portal logout error", e);
            throw new ServerException(
                    PortalError.IO_ERROR, "Failed to logout", e);
        } catch (PortalProtocolException e) {
            /*
             * Wrap protocol exception into a new Platform exception
             * unless trying to logout when user already gone.
             */
            ProtocolError error = e.getProtocolError();
            if (error != ProtocolError.LOGOUT_ALREADY_GONE) {
                PortalError err = errorTranslator.translate(e);
                throw new PlatformException(err, e.getMessage(), e);
            }
        }
        return session;
    }
}
