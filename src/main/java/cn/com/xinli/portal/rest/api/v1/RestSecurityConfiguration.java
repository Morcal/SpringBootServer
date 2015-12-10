package cn.com.xinli.portal.rest.api.v1;

import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.rest.RestSessionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.core.token.TokenService;

import java.security.SecureRandom;
import java.util.Collections;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
@Configuration
@EnableWebSecurity
@Order(101)
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestSecurityConfiguration.class);

    @Value("${private_key}") private String privateKey;

    @Value("${server.integer}") private Integer serverInteger;

    @Bean
    public SessionService restSessionService() {
        return new RestSessionService();
    }

    @Bean
    public TokenService tokenService() {
        SecureRandom random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            log.warn(e);
            random = new SecureRandom();
        }

        random.setSeed(random.generateSeed(64));

        log.info("Creating token service with private key: "
                + privateKey + ", server integer: " + serverInteger);

        KeyBasedPersistenceTokenService service = new KeyBasedPersistenceTokenService();
        service.setServerSecret(privateKey);
        service.setServerInteger(serverInteger);
        service.setSecureRandom(random);

        return service;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(new ChallengeAuthenticationProvider()));
    }

    @Bean
    public RestAuthenticationFilter restAuthenticationFilter() {
        RestAuthenticationFilter filter = new RestAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(restAuthenticationFilter());
    }
}
