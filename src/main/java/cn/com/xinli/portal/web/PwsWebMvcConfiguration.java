package cn.com.xinli.portal.web;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.rest.Scheme;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
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
public class PwsWebMvcConfiguration extends WebMvcConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(PwsWebMvcConfiguration.class);

    @Autowired
    private ServerConfig serverConfig;

    @Bean
    public String pwsSchemeHeaderName() {
        return "X-PWS-Scheme";
    }

    @Bean
    public Scheme pwsScheme() {
        Scheme scheme = new Scheme();
        scheme.setUri(pwsRestApiLocationUri());
        scheme.setVersion("1.0");
        scheme.setServer("192.168.3.26");
        scheme.setPort(8080);
        scheme.setHost("192.168.3.26");
        return scheme;
    }

    @Bean
    public String pwsRestApiLocationUri() {
        return "/" + serverConfig.getApplication() + "/" + serverConfig.getReestApiUri();
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
    public String pwsSchemeHeaderValue(Scheme scheme) {
        StringJoiner joiner = new StringJoiner(";");
        joiner.add("version=" + scheme.getVersion())
                .add("apiuri=" + scheme.getUri())
                .add("server=" + scheme.getServer())
                .add("host=" + scheme.getHost())
                .add("port=" + String.valueOf(scheme.getPort()));
        return joiner.toString();
    }

    @Bean
    public String mainPageViewName() {
        return "/html/main.html";
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
//        registry.viewResolver(decoratedViewResolver());
        registry.viewResolver(viewResolver());
    }
}

