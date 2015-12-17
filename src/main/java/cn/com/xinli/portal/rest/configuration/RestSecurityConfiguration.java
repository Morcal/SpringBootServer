package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.rest.RestAuthenticationFailureEvent;
import cn.com.xinli.portal.rest.RestAuthenticationSuccessEvent;
import cn.com.xinli.portal.rest.SecureRandomStringGenerator;
import cn.com.xinli.portal.rest.api.EntryPoint;
import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.api.Registration;
import cn.com.xinli.portal.rest.auth.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.header.HeaderWriterFilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * PWS REST APIs spring web security configuration.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Configuration
@EnableWebSecurity
@Order(2)
public class RestSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestSecurityConfiguration.class);

    @Autowired
    private Provider restApiProvider;

    @Autowired
    private ServerConfig serverConfig;

    public static final String ACCESS_TOKEN_SCOPE = "portal-rest-api";
    public static final String SESSION_TOKEN_SCOPE = "portal-session";
    public static final String TOKEN_TYPE = "Bearer";

    public static final long MAX_TIME_DIFF = 1800; // seconds.

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public SecureRandomStringGenerator secureKeyGenerator() {
        return new SecureRandomStringGenerator();
    }

    @Bean
    public RestAuthenticationProvider challengeAuthenticationProvider() {
        return new RestAuthenticationProvider();
    }

    @Bean
    public ApplicationListener<RestAuthenticationFailureEvent> restAuthenticationFailureEventHandler() {
        return new RestAuthenticationFailureEventHandler();
    }

    @Bean
    public ApplicationListener<RestAuthenticationSuccessEvent> restAuthenticationSuccessEventHandler() {
        return new RestAuthenticationSuccessEventHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(challengeAuthenticationProvider()));
    }

    @Bean
    public RestAuthenticationFilter restAuthenticationFilter() {
        List<List<String>> list =
        restApiProvider.getRegistrations().stream()
                .map(registration ->
                    registration.getApis().stream()
                            .filter(EntryPoint::requiresAuth)
                            .map(EntryPoint::getUrl)
                            .collect(Collectors.toList()))
                .collect(Collectors.toList());

        Set<String> urls = new HashSet<>();
        list.forEach(strings -> strings.forEach(urls::add));

        urls.forEach(url -> log.debug("> Add filter: " + url));

        RestAuthenticationFilter filter = new RestAuthenticationFilter();
        filter.setContinueFilterChainOnUnsuccessful(false);
        filter.setFilterPathMatches(urls);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* Configure filter as http header filter. */
        http.addFilterAfter(restAuthenticationFilter(), HeaderWriterFilter.class);

        /* Disable csrf. */
        http.csrf().disable();

        /* handle authentication exception. */
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint());

        /* Authenticate REST APIs. */
        for (Registration registration : restApiProvider.getRegistrations()) {
            http.authorizeRequests()
                    .antMatchers("/" + serverConfig.getApplication() + "/" + registration.getVersion())
                    .authenticated();
        }
    }
}
