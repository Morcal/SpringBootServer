package cn.com.xinli.portal;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasService;
import cn.com.xinli.portal.core.runtime.Runtime;
import cn.com.xinli.portal.core.session.SessionService;
import cn.com.xinli.portal.web.filter.AuthenticationFilter;
import cn.com.xinli.portal.web.filter.RateLimitingFilter;
import cn.com.xinli.portal.web.rest.EntryPoint;
import cn.com.xinli.portal.web.rest.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Portal runtime environment.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/31.
 */
@Configuration
@Order(Stage.INITIALIZE)
public class Environment implements ApplicationEventPublisherAware {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Environment.class);

    /** Application event publisher. */
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private NasService nasService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    @Qualifier("rest-api-provider")
    private Provider restApiProvider;

    @Autowired
    @Qualifier("admin-api-provider")
    private Provider adminRestApiProvider;

    @Autowired
    private Runtime runtime;

    /**
     * Set up authentication filter matcher URIs.
     * <p>After matcher URIs been setup, any requests targeting those URIs(matches)
     * will be filter by authentication filter.
     * @param filter filter.
     */
    @Autowired
    public void setupAuthenticationFilterMatchedUris(AuthenticationFilter filter) {
        List<List<String>> list = restApiProvider.getRegistrations().stream()
                .map(registration ->
                        registration.getApis().stream()
                                .filter(EntryPoint::requiresAuth)
                                .map(EntryPoint::getUrl)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<List<String>> admins = adminRestApiProvider.getRegistrations().stream()
                .map(registration ->
                        registration.getApis().stream()
                                .filter(EntryPoint::requiresAuth)
                                .map(EntryPoint::getUrl)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        list.addAll(admins);

        Set<String> urls = new HashSet<>();
        list.forEach(strings -> strings.forEach(urls::add));

        if (logger.isDebugEnabled()) {
            urls.forEach(url -> logger.info("Adding auth filter path: {}.", url));
        }

        filter.setMatchedUris(urls);
        filter.setContinueFilterChainOnUnsuccessful(false);
    }

    /**
     * Set up rate-limiting filter matcher uris.
     * <p>After matcher URIs been setup, any requests targeting those URIs(matches)
     * will be filter by rate-limiting.
     * @param filter filter.
     */
    @Autowired
    public void setupRateLimitingFilterMatchedUris(RateLimitingFilter filter) {
        List<List<String>> list = restApiProvider.getRegistrations().stream()
                .map(registration ->
                        registration.getApis().stream()
                                .map(EntryPoint::getUrl)
                                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        Set<String> urls = new HashSet<>();
        list.forEach(strings -> strings.forEach(urls::add));

        if (logger.isDebugEnabled()) {
            urls.forEach(url -> logger.info("Adding rate-limiting filter path: {}.", url));
        }

        filter.setMatchedUris(urls);
    }

    /**
     * Handle spring-context refreshed event.
     *
     * <p>Server loads NAS/BRAS devices only after spring-context refreshed, otherwise
     * server may encounter 'no session' lazy-initialization exception when access lazy-initial
     * JPA entities, such as {@link Nas}.
     *
     * <p>Server also creates mock-huawei-bras after NAS/BRAS devices already loaded
     * (by calling {@link NasService#init()}.
     *
     * @param event spring-context refreshed event.
     */
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        logger.info("context refresh event: {}", event);
        nasService.init();
        sessionService.init();
        runtime.createDeviceStatistics(nasService.all());

        applicationEventPublisher.publishEvent(new EnvironmentInitializedEvent(this));
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

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

}
