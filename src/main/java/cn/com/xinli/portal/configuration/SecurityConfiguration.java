package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.filter.AuthenticationFilter;
import cn.com.xinli.portal.support.RestAuthenticationProvider;
import cn.com.xinli.portal.support.rest.EntryPoint;
import cn.com.xinli.portal.support.rest.Provider;
import cn.com.xinli.portal.support.rest.Registration;
import cn.com.xinli.portal.support.rest.RestAuthenticationEntryPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PWS spring web security configuration.
 *
 * <p>This class configures spring-web-security.
 * Essential configurations are:
 * <ul>
 *     <li>Authentication filter for REST APIs</li>
 *     <li>Rate limiting filter</li>
 *     <li>{@link AuthenticationProvider}</li>
 * </ul>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/10.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(mode = AdviceMode.PROXY, prePostEnabled = true, proxyTargetClass = true)
@Order(10)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    /** REST tokens type. */
    public static final String TOKEN_TYPE = "Bearer";

    /** Challenge response type. */
    public static final String CHALLENGE_RESPONSE_TYPE = "challenge";

    /** Minimum update time diff in seconds. */
    public static final long MIN_TIME_UPDATE_DIFF = 3; // seconds.

    /** Access token time to live in seconds. */
    public static final int ACCESS_TOKEN_TTL = 35;

    /** Session token time to live in seconds. */
    public static final int SESSION_TOKEN_TTL = 35;

    /** Challenge token time to live in seconds. */
    public static final int CHALLENGE_TTL = 35;

    /** Rate limiting (requests per second). */
    public static final int RATE_LIMITING = 5;

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    private Provider provider;

    @Autowired
    private RestAuthenticationProvider authenticationProvider;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

//    @Bean
//    public MethodSecurityMetadataSource methodSecurityMetadataSource(PrePostInvocationAttributeFactory factory) {
////        return new Jsr250MethodSecurityMetadataSource();
//        return new PrePostAnnotationSecurityMetadataSource(factory);
//        //return new DelegatingMethodSecurityMetadataSource(Collections.emptyList());
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.warn("configuring web security... ");
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("Rest security configuring...");

        /* Configure filter as http header filter. */
        http.addFilterBefore(authenticationFilter, AnonymousAuthenticationFilter.class);

        /* handle authentication exception. */
        http.exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint);

        /* Disable csrf. */
        http.csrf().disable();

        /* Permit all allowed urls (without authentication). */
        List<String> allowedUrls = new ArrayList<>();
        allowedUrls.add("/");
        allowedUrls.add( "/portal");
        allowedUrls.add("/portal/api");

        for (Registration registration : provider.getRegistrations()) {
            allowedUrls.addAll(registration.getApis().stream()
                    .filter(entryPoint -> !entryPoint.requiresAuth())
                    .map(EntryPoint::getUrl)
                    .collect(Collectors.toList()));
        }

        http.authorizeRequests()
                .antMatchers(allowedUrls.toArray(new String[allowedUrls.size()]))
                .permitAll();
    }
}
