package cn.com.xinli.portal.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
@Configuration
@EnableWebSecurity
@Order(1)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfiguration.class);

    @Value("${pws.root}") private String application;

    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.warn("> configuring web security... ");
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.warn("> configuring http security... ");
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/" + application, "/" + application + "/api")
                .permitAll();
    }
}
