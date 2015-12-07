package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.auth.AuthorizationServer;
import cn.com.xinli.portal.auth.AuthorizationServerImpl;
import cn.com.xinli.portal.rest.api.RestApiProvider;
import cn.com.xinli.portal.rest.RestSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
@ImportResource("classpath:nas.xml")
@PropertySource("pws.properties")
public class PortalConfig {
    @Autowired
    private RestApiProvider restApiProvider;

    @Value("${pws.private_key}") private String privateKey;

    @Bean
    public AuthorizationServer authorizationServer() {
        return new AuthorizationServerImpl();
    }

    @Bean
    public SessionService sessionService() {
        RestSessionService service = new RestSessionService();
        return service;
    }

    @Bean
    public NasMapping deviceMapping() {
        NasMapping mapping = new NasMapping();
        return mapping;
    }
}
