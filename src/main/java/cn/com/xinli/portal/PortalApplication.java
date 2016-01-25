package cn.com.xinli.portal;

import cn.com.xinli.portal.core.*;
import cn.com.xinli.portal.service.CertificateService;
import cn.com.xinli.portal.service.NasService;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.PortalServerConfig;
import cn.com.xinli.portal.transport.huawei.HuaweiPortal;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Collections;

/**
 * Portal Spring-Boot Application.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/11/30.
 */
@SpringBootApplication
@PropertySource("pws.properties")
@ComponentScan
public class PortalApplication {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalApplication.class);

    @Value("${pws.portal.server.listen.port}") private int portalServerListenPort;

    @Value("${pws.portal.server.thread.size}") private int portalServerThreadSize;

    @Value("${pws.portal.server.shared_secret}") private String portalServerSharedSecret;

    @Value("${pws.nas.huawei.mock.enable}") private boolean enableMockHuaweiNas;

    @Value("${pws.nas.huawei.mock.nasid}") private String mockHuaweiNasId;

    @Bean
    public Activity.Severity minimalSeverity() {
        return Activity.Severity.NORMAL;
    }

    @Autowired
    private InternalServerHandler internalServerHandler;

    @Autowired
    private NasManager nasManager;

    @Autowired
    private NasService nasService;

    @Autowired
    private CertificateManager certificateManager;

    @Autowired
    private CertificateService certificateService;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PortalApplication.class, args);
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

    @Bean
    Certificate jportalCertificate() {
        try {
            return certificateService.loadCertificate("jportal");
        } catch (CertificateNotFoundException e) {
            logger.debug("jportal not exist");
            return certificateManager.create("jportal", "xinli", "android", "v1.0", "s3cr3t");
        }
    }

    private Nas createNas(String nasName) {
        CredentialsModifier domain = new CredentialsModifier();
        domain.setPosition(CredentialsModifier.Position.TAIL);
        domain.setTarget(CredentialsModifier.Target.USERNAME);
        domain.setValue("@xinli");

        CredentialsEncoder noOp = new CredentialsEncoders.NoOpEncoder();

        CredentialsTranslation translation = new CredentialsTranslation();
        translation.setModifiers(Collections.singletonList(domain));
        translation.setEncoder(noOp);

        Nas nas = nasManager.createNas(nasName, "127.0.0.1", null, NasType.HuaweiV2,
                2000, AuthType.CHAP, "s3cr3t", translation);

        if (logger.isDebugEnabled()) {
            logger.debug("nas created: {}", nas);
        }

        return nas;
    }

    private void ensureNasConfiguration(String nasName) {
        if (StringUtils.isEmpty(nasName)) {
            throw new IllegalArgumentException("NAS name can not be blank.");
        }

        try {
            Nas nas = nasService.find(nasName);
            logger.debug("NAS found, {}", nas);
        } catch (NasNotFoundException e) {
            logger.debug("NAS not found, name: {}", nasName);
            Nas nas = createNas(nasName);
            nasManager.createNasIpv4RangeRule(nas, "192.168.3.1", "192.168.3.254");
            nasService.reload();
        }
    }

    /**
     * Define portal server (receiving request from NAS).
     *
     * <p>If Huawei NAS is enabled for developing purpose,
     * This bean will also create a {@link PortalServer} for Huawei NAS.
     *
     * @param nasContainer NAS container.
     * @return internal portal server.
     * @throws NasNotFoundException
     */
    @Autowired
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public PortalServer portalServer(NasContainer nasContainer) throws NasNotFoundException {
        if (enableMockHuaweiNas) {
            ensureNasConfiguration(mockHuaweiNasId);

            /* Find nas configuration for mocking. */
            Nas mockNas = nasContainer.find(mockHuaweiNasId);

            PortalServer nas = HuaweiPortal.createNas(mockNas);
            try {
                nas.start();
            } catch (IOException e) {
                logger.error("Failed to start mock huawei nas", e);
            }
        }
        PortalServerConfig portalServerConfig = new PortalServerConfig();
        portalServerConfig.setListenPort(portalServerListenPort);
        portalServerConfig.setSharedSecret(portalServerSharedSecret);
        portalServerConfig.setThreadSize(portalServerThreadSize);

        return HuaweiPortal.createServer(portalServerConfig, internalServerHandler);
    }
}
