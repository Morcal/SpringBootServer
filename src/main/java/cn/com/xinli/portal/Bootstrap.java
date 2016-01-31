package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.core.nas.NasService;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.support.InterProcessNpsSessionProvider;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Portal web server bootstrap.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
@Configuration
@Order(Stage.CONFIGURE)
public class Bootstrap {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    @Bean
    public PropertiesServerConfiguration propertiesServerConfiguration() {
        return new PropertiesServerConfiguration();
    }

    @Bean
    public ServerConfiguration serverConfiguration() throws ServerException {
        return propertiesServerConfiguration().loadFromProperties("classpath:pws.properties");
    }

    @Bean
    public List<SessionProvider> sessionProviders() {
        List<SessionProvider> providers = new ArrayList<>();
        providers.add(HuaweiPortal.createSessionProvider());
        providers.add(new InterProcessNpsSessionProvider());
        return providers;
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
    @Order(Stage.INITIALIZE)
    public void handleContextRefresh(ContextRefreshedEvent event) throws Exception {
        logger.info("context refresh event: {}", event);
        ApplicationContext context = event.getApplicationContext();
        context.getBean(NasService.class).init();

        context.getBean("internalPortalServer", PortalServer.class).start();
    }
}
