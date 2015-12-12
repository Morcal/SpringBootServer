package cn.com.xinli.portal.rest.api.v1.configuration;

import cn.com.xinli.portal.SessionService;
import cn.com.xinli.portal.rest.RestSessionService;
import cn.com.xinli.portal.rest.api.v1.SecureKeyGenerator;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeAuthenticationProvider;
import cn.com.xinli.portal.rest.api.v1.auth.RestAuthenticationFilter;
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
import org.springframework.security.web.header.HeaderWriterFilter;

import java.security.SecureRandom;
import java.util.Collections;

/**
 * Project: portal
 *
 * TODO check configuration order.
 *
 * @author zhoupeng 2015/12/10.
 */
@Configuration
@EnableWebSecurity
@Order(101)
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestSecurityConfiguration.class);

    public static final String REST_API_VERSION = "v1.0";
    public static final String REST_API_ENTRANCE = "session";
    public static final String REST_API_SESSION = "session";
    public static final String REST_API_SESSIONS = "sessions";
    public static final String REST_API_AUTHORIZE = "authorize";

    public static final String REST_API_URL = "/${application}/" + REST_API_VERSION + "/" + REST_API_ENTRANCE;
    public static final String REST_API_AUTHORIZE_URL = "/${application}/" + REST_API_VERSION + "/" + REST_API_AUTHORIZE;
    public static final String REST_API_SESSION_URL = "/${application}/" + REST_API_VERSION + "/" + REST_API_SESSION;
    public static final String REST_API_SESSIONS_URL = "/${application}/" + REST_API_VERSION + "/" + REST_API_SESSIONS;

    @Value("${private_key}") private String privateKey;

    @Value("${server.integer}") private Integer serverInteger;

    @Value("${application}") private String application;

    @Bean
    public SecureKeyGenerator secureKeyGenerator() {
        return new SecureKeyGenerator();
    }

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
        String[] matches = new String[] {
                REST_API_URL,
                REST_API_AUTHORIZE_URL,
                REST_API_SESSION_URL,
                REST_API_SESSIONS_URL };
        RestAuthenticationFilter filter = new RestAuthenticationFilter();
        filter.setContinueFilterChainOnUnsuccessful(false);
        filter.setFilterPathMatches(matches);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(restAuthenticationFilter(), HeaderWriterFilter.class);
    }
}
