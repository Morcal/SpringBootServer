package cn.com.xinli.portal.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
@Configuration
public class PortalWebServerMvcConfiguration extends WebMvcConfigurerAdapter {
    private static final Log log = LogFactory.getLog(PortalWebServerMvcConfiguration.class);
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        log.warn(">>>> configuring view resolvers...");
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/public");
        viewResolver.setSuffix(".html");
        viewResolver.setOrder(2);
        registry.viewResolver(viewResolver);
    }
}
