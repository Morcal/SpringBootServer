package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.rest.api.RestApiProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/10.
 */
//@Configuration
public class PortalMvcConfig extends WebMvcConfigurerAdapter {
    //@Autowired
    private RestApiProvider restApiProvider;

    @Value("${application}")
    private String application;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        final String rootUrl = "/" + application;
        restApiProvider.getRegistrations().stream().forEach(reg -> {
            final String baseUrl = rootUrl + "/" + reg.getVersion();
            reg.getApis().stream().forEach(api -> registry.addViewController(baseUrl + api.getUrl()));
        });
    }
}
