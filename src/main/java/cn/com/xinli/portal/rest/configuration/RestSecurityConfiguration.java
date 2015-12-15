package cn.com.xinli.portal.rest.configuration;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: portal
 *
 * TODO check configuration order.
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

    @Value("${private_key}") private String privateKey;

    @Value("${server.integer}") private Integer serverInteger;

    @Value("${application}") private String application;

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
                    registration.getApis().stream().map(EntryPoint::getUrl)
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

        /* Disable csrf and handle authentication exception. */
        http.csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint());

        /* Authenticate REST APIs. */
        for (Registration registration : restApiProvider.getRegistrations()) {
            http.authorizeRequests()
                    .antMatchers("/" + application + "/" + registration.getVersion())
                    .authenticated();
        }
    }
}
