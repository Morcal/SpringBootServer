package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.rest.SecureRandomStringGenerator;
import cn.com.xinli.portal.rest.api.EntryPoint;
import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.auth.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.session.ConcurrentSessionFilter;

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
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(10)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(SecurityConfiguration.class);

    @Autowired
    private Provider restApiProvider;

    public static final String PORTAL_USER_ROLE = "ROLE_USER";
    public static final String SYSTEM_ADMIN_ROLE = "ROLE_SYSADM";
    public static final String SESSION_TOKEN_SCOPE = "portal-session";
    public static final String TOKEN_TYPE = "Bearer";

    public static final String SPRING_EL_PORTAL_USER_ROLE = " hasRole('" + PORTAL_USER_ROLE + "') ";
    public static final String SPRING_EL_SYSTEM_ADM_ROLE = " hasRole('" + SYSTEM_ADMIN_ROLE + "') ";

    /** Challenge response type. */
    public static final String CHALLENGE_RESPONSE_TYPE = "challenge";

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
    public AuthenticationProvider authenticationProvider() {
        return new RestAuthenticationProvider();
    }

    @Bean
    public ApplicationListener<AuthenticationFailureEvent> restAuthenticationFailureEventHandler() {
        return new AuthenticationFailureEventHandler();
    }

    @Bean
    public ApplicationListener<AuthenticationSuccessEvent> restAuthenticationSuccessEventHandler() {
        return new AuthenticationSuccessEventHandler();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider()));
    }

    @Bean
    public AuthenticationFilter restAuthenticationFilter() {
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

        if (log.isDebugEnabled()) {
            urls.forEach(url -> log.debug("> Add filter: " + url));
        }

        AuthenticationFilter filter = new AuthenticationFilter();
        filter.setContinueFilterChainOnUnsuccessful(false);
        filter.setFilterPathMatches(urls);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /* Configure filter as http header filter. */
        http.addFilterAfter(restAuthenticationFilter(), ConcurrentSessionFilter.class);

        http.authenticationProvider(authenticationProvider());

        /* Disable anonymous authentication. */
        http.anonymous().disable();

        /* handle authentication exception. */
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint());

        /* Disable csrf. */
        http.csrf().disable();

        http.authorizeRequests().anyRequest().authenticated();
    }
}
