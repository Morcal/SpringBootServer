package cn.com.xinli.portal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Portal Spring-Boot Application.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
@SpringBootApplication
public class PortalApplication {

    private final Logger logger = LoggerFactory.getLogger(PortalApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PortalApplication.class, args);
    }

    @Bean
    protected ServletContextListener listener() {
        logger.debug("> create servlet context listener.");
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
}
