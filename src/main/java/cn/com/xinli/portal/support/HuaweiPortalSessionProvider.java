package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PlatformException;
import cn.com.xinli.portal.core.PortalError;
import cn.com.xinli.portal.core.PortalException;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.nas.HuaweiNas;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasType;
import cn.com.xinli.portal.core.session.Session;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.transport.Connector;
import cn.com.xinli.portal.transport.TransportError;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.huawei.*;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
import cn.com.xinli.portal.transport.huawei.support.HuaweiRequestContextSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

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

    private final HuaweiRequestContextSerializer serializer = new HuaweiRequestContextSerializer();

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
        session.setCredentials(credentials);
        return session;
    }

    /**
     * Create endpoint for remote nas.
     * @param nas nas.
     * @return endpoint.
     * @throws UnknownHostException
     */
    static Endpoint endpointOf(HuaweiNas nas) throws UnknownHostException {
        return Endpoint.of(
                Version.valueOf(nas.getVersion()),
                nas.getNetworkAddress(),
                nas.getListenPort(),
                AuthType.valueOf(nas.getAuthType()),
                nas.getSharedSecret());
    }

    @Override
    public Session authenticate(Nas nas, Credentials credentials) throws PortalException {
        Objects.requireNonNull(nas, Nas.EMPTY_NAS);
        Objects.requireNonNull(credentials, Credentials.EMPTY_CREDENTIALS);

        try {
            HuaweiNas huaweiNas = HuaweiNas.class.cast(nas);
            Session session = createSession(nas, credentials);
            Connector<RequestContext> client = HuaweiPortal.getConnector(endpointOf(huaweiNas));
            RequestContext ctx = client.login(session.getCredentials());
            Optional<String> ext = serializer.serialize(ctx);
            ext.orElseThrow(() ->
                    new ServerException(
                            PortalError.SERVER_INTERNAL_ERROR,
                            "session extended information serialize failure"));
            session.setExtendedInformation(ext.get());
            return session;
        } catch (IOException e) {
            logger.error("Portal login error", e);
            throw new ServerException(PortalError.IO_ERROR, "Failed to login", e);
        } catch (TransportException e) {
            PortalError err = errorTranslator.translate(e);
            throw new PlatformException(err, e.getMessage(), e);
        }
    }

    @Override
    public void disconnect(Session session) throws PortalException {
        Objects.requireNonNull(session, Session.EMPTY_SESSION);
        HuaweiNas huaweiNas = HuaweiNas.class.cast(session.getNas());

        try {
            Connector<RequestContext> client = HuaweiPortal.getConnector(endpointOf(huaweiNas));
            String extendedInformation = session.getExtendedInformation();
            Optional<RequestContext> ctx = serializer.deserialize(extendedInformation);
            ctx.orElseThrow(() ->
                    new ServerException(
                            PortalError.SERVER_INTERNAL_ERROR,
                            "session extended information deserialize failure"));
            client.logout(session.getCredentials(), ctx.get());
        } catch (IOException e) {
            logger.error("Portal logout error", e);
            throw new ServerException(
                    PortalError.IO_ERROR, "Failed to logout", e);
        } catch (TransportException e) {
            /*
             * Wrap protocol exception into a new Platform exception
             * unless trying to logout when user already gone.
             */
            TransportError error = e.getProtocolError();
            if (error != TransportError.LOGOUT_ALREADY_GONE) {
                PortalError err = errorTranslator.translate(e);
                throw new PlatformException(err, e.getMessage(), e);
            }
        }
    }
}
