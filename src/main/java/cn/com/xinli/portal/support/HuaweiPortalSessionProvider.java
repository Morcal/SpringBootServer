package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PlatformException;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.HuaweiCredentials;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasType;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.transport.PortalClient;
import cn.com.xinli.portal.transport.PortalProtocolException;
import cn.com.xinli.portal.transport.ProtocolError;
import cn.com.xinli.portal.transport.Result;
import cn.com.xinli.portal.core.nas.HuaweiNas;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Objects;

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
        return nas != null && nas.getType() != null && nas.getType() == NasType.HUAWEI;
    }

    /**
     * Create a session.
     * @param nas nas.
     * @param credentials user credentials.
     * @return session.
     * @throws UnknownHostException
     */
    Session createSession(Nas nas, Credentials credentials) throws UnknownHostException {
        Session session = new Session();
        session.setNas(nas);
        session.setCredentials(from(credentials));
        return session;
    }

    /**
     * Create huawei credentials from other credentials.
     * @param credentials other credentials.
     * @return huawei credentials.
     */
    static HuaweiCredentials from(Credentials credentials) {
        return HuaweiCredentials.of(
                credentials.getUsername(),
                credentials.getPassword(),
                credentials.getIp(),
                credentials.getMac(),
                0);
    }

    static Endpoint from(HuaweiNas nas) throws UnknownHostException {
        return Endpoint.of(
                Version.valueOf(nas.getVersion()),
                nas.getNetworkAddress(),
                nas.getListenPort(),
                AuthType.valueOf(nas.getAuthType()),
                nas.getSharedSecret());
    }

    @Override
    public Session authenticate(Nas nas, Credentials credentials) throws PortalException {
        Objects.requireNonNull(nas);
        Objects.requireNonNull(credentials);

        try {
            HuaweiNas huaweiNas = HuaweiNas.class.cast(nas);
            Session session = createSession(nas, credentials);
            PortalClient client = HuaweiPortal.createClient(from(huaweiNas));
            Result result = client.login(session.getCredentials());

            if (logger.isDebugEnabled()) {
                logger.debug("Portal login result: {}", result);
            }

            return session;
        } catch (IOException e) {
            logger.error("Portal login error", e);
            throw new ServerException(PortalError.IO_ERROR, "Failed to login", e);
        } catch (PortalProtocolException e) {
            PortalError err = errorTranslator.translate(e);
            throw new PlatformException(err, e.getMessage(), e);
        }
    }

    @Override
    public Session disconnect(Session session) throws PortalException {
        Objects.requireNonNull(session);
        HuaweiNas huaweiNas = HuaweiNas.class.cast(session.getNas());

        try {
            PortalClient client = HuaweiPortal.createClient(from(huaweiNas));
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
