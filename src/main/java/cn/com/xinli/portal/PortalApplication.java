package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.core.configuration.PortalServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.nas.NasNotFoundException;
import cn.com.xinli.portal.support.InternalServerHandler;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Version;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Portal Spring-Boot Application.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/11/30.
 */
@SpringBootApplication
@ComponentScan
public class PortalApplication {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(PortalApplication.class);

    @Autowired
    private InternalServerHandler internalServerHandler;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Bean
    public Activity.Severity minimalSeverity() {
        return Activity.Severity.INFO;
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

    /**
     * Define portal server (receiving request from NAS).
     *
     * <p>Since portal server in web server only accepts NTF_LOGOUT requests,
     * some of portal server's endpoint members are not necessary.
     *
     * @return internal portal server.
     * @throws NasNotFoundException
     */
    @Bean(name = "internalPortalServer", destroyMethod = "shutdown")
    @Order(Stage.SERVE)
    public PortalServer portalServer() throws NasNotFoundException, ServerException, UnknownHostException {
        PortalServerConfiguration config = serverConfiguration.getPortalServerConfiguration();
        Endpoint endpoint = new Endpoint();
        endpoint.setPort(config.getPort());
        endpoint.setSharedSecret(config.getSharedSecret());
        endpoint.setVersion(Version.valueOf(config.getVersion()));
        endpoint.setAddress(InetAddress.getByName(config.getHost()));
        return HuaweiPortal.createServer(endpoint, internalServerHandler);
    }
}
