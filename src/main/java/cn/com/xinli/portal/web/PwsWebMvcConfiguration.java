package cn.com.xinli.portal.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Configuration
@EnableWebMvc
public class PwsWebMvcConfiguration extends WebMvcConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(PwsWebMvcConfiguration.class);

    @Bean
    public String pwsRestApiLocationHeader() {
        return "X-PWS-Location";
    }

    @Bean
    public InternalResourceViewResolver internalResourceViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/static/");
        resolver.setSuffix(".html");
        log.debug("> configuring view resolvers.");
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/WEB-INF/static");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    //    @Override
//    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.viewResolver(internalResourceViewResolver());
//    }
}

