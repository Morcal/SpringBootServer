package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.certificate.Certificate;
import cn.com.xinli.portal.core.certificate.CertificateManager;
import cn.com.xinli.portal.core.certificate.CertificateNotFoundException;
import cn.com.xinli.portal.core.certificate.CertificateService;
import cn.com.xinli.portal.core.configuration.PortalServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.credentials.CredentialsEncoder;
import cn.com.xinli.portal.core.credentials.CredentialsEncoders;
import cn.com.xinli.portal.core.credentials.CredentialsModifier;
import cn.com.xinli.portal.core.credentials.CredentialsTranslation;
import cn.com.xinli.portal.core.nas.*;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.AuthType;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

/**
 * Portal runtime environment.
 *
 * Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Configuration
@Order(Stage.INITIALIZE)
public class Environment implements ApplicationEventPublisherAware {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Environment.class);

    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private NasService nasService;

    @Autowired
    private InternalServerHandler internalServerHandler;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Bean
    public Activity.Severity minimalSeverity() {
        return serverConfiguration.getActivityConfiguration().getSeverity();
    }

    /**
     * Handle spring-context refreshed event.
     *
     * <p>Server loads NAS/BRAS devices only after spring-context refreshed, otherwise
     * server may encounter 'no session' lazy-initialization exception when access lazy-initial
     * JPA entities, such as {@link Nas}.
     *
     * <p>Server also creates mock-huawei-bras after NAS/BRAS devices already loaded
     * (by calling {@link NasService#init()}.
     *
     * @param event spring-context refreshed event.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        logger.info("context refresh event: {}", event);
        nasService.init();

        applicationEventPublisher.publishEvent(new EnvironmentInitializedEvent(this));
    }

    @Bean
    protected ServletContextListener listener() {
        logger.debug("create servlet context listener.");
        return new ServletContextListener() {
            @Override
            public void contextInitialized(ServletContextEvent sce) {
                logger.info("ServletContext initialized");
            }

            @Override
            public void contextDestroyed(ServletContextEvent sce) {
                logger.info("ServletContext destroyed");
            }
        };
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
    public PortalServer portalServer() throws NasNotFoundException, ServerException, UnknownHostException {
        PortalServerConfiguration config = serverConfiguration.getPortalServerConfiguration();
        Endpoint endpoint = new Endpoint();
        endpoint.setPort(config.getPort());
        endpoint.setSharedSecret(config.getSharedSecret());
        endpoint.setVersion(Version.valueOf(config.getVersion()));
        endpoint.setAddress(InetAddress.getByName(config.getHost()));
        return HuaweiPortal.createServer(endpoint, internalServerHandler);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
