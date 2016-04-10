package cn.com.xinli.portal.web.configuration;

import cn.com.xinli.portal.core.configuration.RestConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.ServerConfigurationService;
import cn.com.xinli.portal.support.configuration.ApiConfiguration;
import cn.com.xinli.portal.web.rest.Scheme;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.RedirectView;

import java.util.concurrent.TimeUnit;

/**
 * PWS web mvc configuration.
 *
 * <p>Spring-web-mvc configuration enable supports for
 * <ul>
 *     <li>static contents</li>
 *     Configure a {@link WebContentInterceptor} for path "/static/*"
 *     <li>JSP support</li>
 *     Configure a {@link ViewResolver} for path "/WEB-INF/jsp/"
 *     <li>cache control</li>
 *     <li>REST API scheme</li>
 *     Define a {@link Scheme} bean via {@link #scheme()}
 *     <li>Default {@link #mainPageView()} and {@link #errorPageView()}</li>
 *     <li>static resources</li>
 *     Configure {@link ResourceHandlerRegistry}s for
 *     "/html/**" with "classpath:/WEB-INF/static/html/"
 *     "/css/**" with "classpath:/WEB-INF/static/css/"
 * </ul>
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
public class MvcConfiguration extends WebMvcConfigurerAdapter /*WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter*/ {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(MvcConfiguration.class);

    private static final int DEFAULT_WEB_SERVER_PORT = 80;

    @Autowired
    private ServerConfigurationService serverConfigurationService;

    @EventListener
    public void onEmbeddedServletContainerInitialized(EmbeddedServletContainerInitializedEvent event) {
        int port = event.getEmbeddedServletContainer().getPort();
        scheme().setPort(port);
    }

    @Bean
    public Scheme scheme() {
        RestConfiguration config = serverConfigurationService.getServerConfiguration().getRestConfiguration();
        Scheme scheme = new Scheme();
        scheme.setUri(ApiConfiguration.API_PATH);
        scheme.setVersion("1.0");
        scheme.setServer(config.getServer());
        scheme.setHost(config.getHost());
        scheme.setScheme(config.getScheme());
        scheme.setHeader(config.getHeader());
        scheme.setMeta(config.getMeta());
        scheme.setPort(DEFAULT_WEB_SERVER_PORT);
        return scheme;
    }

    @Bean
    public View mainPageView() {
        final ServerConfiguration serverConfiguration = serverConfigurationService.getServerConfiguration();
        if (!StringUtils.isEmpty(serverConfiguration.getMainPageRedirectUrl())) {
            return new RedirectView(serverConfiguration.getMainPageRedirectUrl());
        } else {
            return new InternalResourceView("/html/main.html");
        }
    }

    @Bean
    public View errorPageView() {
        return new InternalResourceView("/json/error.json");
    }

    @Bean(name = "admin-page")
    public View adminPageView() {
        return new InternalResourceView("/html/admin.html");
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        logger.debug("configuring jsp view resolvers.");
        return resolver;
    }

    @Bean
    public CacheControl cacheControl() {
        return CacheControl.maxAge(31556926, TimeUnit.SECONDS);
    }

    @Bean
    public WebContentInterceptor webContentInterceptor() {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.addCacheMapping(cacheControl(), "/static/*");
        return webContentInterceptor;
    }

    /**
     * The default error page view.
     *
     * <p>By default, all unhandled exceptions thrown from web application modules
     * will be handled here, and PWS will display an error page.</p>
     *
     * <p>REST modules should handle unhandled exceptions thrown from REST modules
     * differently and separately.</p>
     *
     * @return default error view.
     */
    @Bean
    //@Profile(value = "dev")
    public View defaultErrorView() {
       return new InternalResourceView("/html/error.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webContentInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/html/**")
                .addResourceLocations("classpath:/WEB-INF/static/html/");
        registry.addResourceHandler("/css/**").
                addResourceLocations("classpath:/WEB-INF/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/WEB-INF/static/js/");
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/WEB-INF/static/fonts/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/WEB-INF/static/img/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        registry.enableContentNegotiation(true);
        registry.viewResolver(viewResolver());
    }
}

