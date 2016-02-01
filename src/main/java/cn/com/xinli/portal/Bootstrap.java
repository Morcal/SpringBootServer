package cn.com.xinli.portal;

import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.core.configuration.support.PropertiesServerConfiguration;
import cn.com.xinli.portal.core.session.SessionProvider;
import cn.com.xinli.portal.support.InterProcessNpsSessionProvider;
import cn.com.xinli.portal.transport.huawei.nio.HuaweiPortal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        logger.info("Session providers loaded, {}", providers.size());
        return providers;
    }
}
