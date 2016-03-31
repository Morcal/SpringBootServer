package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.PortalServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.support.HuaweiPortalSessionProvider;
import cn.com.xinli.portal.support.InterProcessNpsSessionProvider;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.support.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Portal web server bootstrap.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
@org.springframework.context.annotation.Configuration
@Order(Stage.CONFIGURE)
public class Bootstrap {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @Bean
    public ServerConfiguration serverConfiguration() {
        return serverConfigurationService.getServerConfiguration();
    }

    /**
     * Setup system supported session providers.
     * @return list of session providers.
     */
    @Bean
    public List<SessionProvider> sessionProviders() {
        List<SessionProvider> providers = new ArrayList<>();
        providers.add(new HuaweiPortalSessionProvider());
        providers.add(new InterProcessNpsSessionProvider());
        logger.info("Session providers loaded, {}", providers.size());
        return providers;
    }


    /**
     * Define portal server (receiving request from NAS).
     *
     * <p>Since portal server in web server only accepts NTF_LOGOUT requests,
     * some of portal server's endpoint members are not necessary.
     *
     * @return internal portal server.
     * @throws NasNotFoundException
     */
    @Bean(name = "internalPortalServer", initMethod = "start", destroyMethod = "shutdown")
    @Autowired
    public PortalServer portalServer(InternalServerHandler internalServerHandler)
            throws NasNotFoundException, ServerException, UnknownHostException {
        PortalServerConfiguration config =
                serverConfigurationService.getServerConfiguration().getPortalServerConfiguration();
        Endpoint endpoint = new Endpoint();
        endpoint.setPort(config.getPort());
        endpoint.setSharedSecret(config.getSharedSecret());
        endpoint.setVersion(Version.valueOf(config.getVersion()));
        endpoint.setAddress(InetAddress.getByName(config.getHost()));
        return HuaweiPortal.createServer(endpoint, internalServerHandler);
    }
}
