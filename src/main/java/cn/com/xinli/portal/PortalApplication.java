package cn.com.xinli.portal;

import cn.com.xinli.portal.protocol.huawei.DefaultPortalServer;
import cn.com.xinli.portal.protocol.huawei.HuaweiNas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.util.Optional;

/**
 * Portal Spring-Boot Application.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
@SpringBootApplication
@ImportResource("classpath:nas.xml")
@PropertySource("pws.properties")
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

    @Autowired
    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public DefaultPortalServer portalServer(NasMapping nasMapping, SessionService sessionService) throws NasNotFoundException {
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

        return new DefaultPortalServer(portalServerConfig, sessionService);
    }
}
