package cn.com.xinli.portal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Portal Spring-Boot Application.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/11/30.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class PortalApplication {

    private static Log logger = LogFactory.getLog(PortalApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(PortalApplication.class, args);
    }

//    @Bean
//    protected ViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("/public");
//        viewResolver.setSuffix(".html");
//        viewResolver.setOrder(2);
//        logger.warn(">>>> create view resolver. <<<<");
//        return viewResolver;
//    }

    @Bean
    protected ServletContextListener listener() {
        logger.warn(">>>> create servlet context listener. <<<<");
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
