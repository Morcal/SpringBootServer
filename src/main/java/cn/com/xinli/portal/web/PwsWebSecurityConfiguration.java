package cn.com.xinli.portal.web;

import cn.com.xinli.portal.ServerConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PwsWebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    /** Log. */
    private static final Log log = LogFactory.getLog(PwsWebSecurityConfiguration.class);

    @Autowired
    private ServerConfig serverConfig;

    @Override
    public void configure(WebSecurity web) throws Exception {
        log.warn("> configuring web security... ");
        web.ignoring().antMatchers("/static/**").and().ignoring().antMatchers("/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.warn("> configuring http security... ");
        http.authorizeRequests().antMatchers("/" + serverConfig.getApplication()).permitAll()
                .and().authorizeRequests().antMatchers("/" + serverConfig.getApplication() + "/api").permitAll();
    }
}
