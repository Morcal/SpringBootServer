package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.protocol.huawei.DefaultPortalServer;
import cn.com.xinli.portal.protocol.huawei.HuaweiNas;
import cn.com.xinli.portal.support.ActivityServiceSupport;
import cn.com.xinli.portal.support.EhCacheSessionDataStore;
import cn.com.xinli.portal.support.SessionServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.Optional;

/**
 * PWS configuration.
 *
 * <p>This configuration should be able to run without
 * any REST related configurations or implementations.
 * I.e. running as a web-page-only server.
 * </p>
 *
 * <p><strong>DO NOT introduce any dependencies from REST packages.</strong>
 * </p>
 *
 * Project: xpws.
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
@ImportResource("classpath:nas.xml")
@PropertySource("pws.properties")
public class PortalConfiguration {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalConfiguration.class);

    @Value("${pws.portal.server.listen.port}") private int portalServerListenPort;

    @Value("${pws.portal.server.thread.size}") private int portalServerThreadSize;

    @Value("${pws.portal.server.shared_secret}") private String portalServerSharedSecret;

    @Value("${pws.nas.huawei.mock.enable}") private boolean enableMockHuaweiNas;

    @Value("${pws.nas.huawei.mock.nasid}") private String mockHuaweiNasId;

    @Bean
    public Activity.Severity minimalSeverity() {
        return Activity.Severity.NORMAL;
    }

    @Bean
    public SessionService sessionService() {
        return new SessionServiceSupport();
    }

    @Bean
    public SessionManager sessionManager() {
        return (SessionManager) sessionService();
    }

    @Bean
    public ActivityService activityService() {
        return new ActivityServiceSupport();
    }

    @Bean(name = "sessionStore", initMethod = "init")
    public SessionStore sessionStore() {
        return new EhCacheSessionDataStore();
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultPortalServer portalServer(NasMapping nasMapping) throws NasNotFoundException {
        if (enableMockHuaweiNas) {
            /* Find nas configuration for mocking. */
            Optional<Nas> mockNas = nasMapping.getNasByNasId(mockHuaweiNasId);
            mockNas.orElseThrow(() -> new NasNotFoundException("nas with id: " + mockHuaweiNasId + " not found."));

            HuaweiNas nas = new HuaweiNas(mockNas.get());
            try {
                nas.start();
            } catch (IOException e) {
                logger.error("Failed to start mock huawei nas", e);
            }
        }
        PortalServerConfig portalServerConfig = new PortalServerConfig();
        portalServerConfig.setPortalServerListenPort(portalServerListenPort);
        portalServerConfig.setPortalServerSharedSecret(portalServerSharedSecret);
        portalServerConfig.setPortalServerThreadSize(portalServerThreadSize);

        return new DefaultPortalServer(portalServerConfig, sessionService());
    }
}
