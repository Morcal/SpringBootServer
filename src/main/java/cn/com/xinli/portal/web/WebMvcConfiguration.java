package cn.com.xinli.portal.web;

import cn.com.xinli.portal.rest.Scheme;
import cn.com.xinli.portal.rest.configuration.ApiConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/15.
 */
@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(WebMvcConfiguration.class);

    @Bean
    public String schemeHeaderName() {
        return "X-PWS-Scheme";
    }

    @Bean
    public Scheme scheme() {
        Scheme scheme = new Scheme();
        scheme.setUri(ApiConfiguration.API_PATH);
        scheme.setVersion("1.0");
        scheme.setServer("192.168.3.26");
        scheme.setPort(8080);
        scheme.setHost("192.168.3.26");
        scheme.setScheme("http");
        return scheme;
    }

    /**
     * The PWS scheme header value.
     *
     * <p>Header string only supports ISO-8859-1 character set.
     * DO NOT try to set non-ASCII character inside header value string.
     * </p>
     * @param scheme application scheme.
     * @return scheme content string.
     */
    @Bean
    public String schemeHeaderValue(Scheme scheme) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("version=" + scheme.getVersion())
                .add("apiuri=" + scheme.getUri())
                .add("server=" + scheme.getServer())
                .add("host=" + scheme.getHost())
                .add("scheme=" + scheme.getScheme())
                .add("port=" + String.valueOf(scheme.getPort()));
        return joiner.toString();
    }

    @Bean
    public View mainPageView() {
        return new InternalResourceView("/html/main.html");
    }

    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        log.debug("> configuring jsp view resolvers.");
        return resolver;
    }

    @Bean CacheControl cacheControl() {
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
     * @see cn.com.xinli.portal.rest.configuration.RestConfiguration
     * @return default error view.
     */
    @Bean
    //@Profile(value = "dev")
    public View defaultErrorView() {
       return new InternalResourceView("/html/error.html");
    }

//    /**
//     * Default exception resolver.
//     *
//     * <p>By default, all unhandled exceptions thrown from web application modules
//     * will be handled here, and PWS will display an error page.</p>
//     *
//     * <p>REST modules should handle unhandled exceptions thrown from REST modules
//     * differently and separately.</p>
//     *
//     * @see cn.com.xinli.portal.rest.RestExceptionAdvisor
//     * @return {@link HandlerExceptionResolver}
//     */
//    @Bean
//    public HandlerExceptionResolver simpleMappingExceptionResolver() {
//        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
//        Properties mapping = new Properties();
//        mapping.setProperty("PortalException", "error");
//        resolver.setExceptionMappings(mapping);
//        resolver.setDefaultErrorView(WebExceptionAdvisor.DEFAULT_ERROR_VIEW);
//        return resolver;
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(webContentInterceptor());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/html/**").addResourceLocations("classpath:/WEB-INF/static/html/");
        registry.addResourceHandler("/css/**").addResourceLocations("classpath:/WEB-INF/static/css/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(viewResolver());
    }

}

