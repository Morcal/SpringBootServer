package cn.com.xinli.portal.configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
public class ApiProviderConfig {
    @Autowired
    private ApiRegistration restApiRegistration;

    @Bean
    public ApiProvider apiProvider() {
        ApiProvider provider = new ApiProvider("Xinli Software Technology ltd., co.");
        try {
            provider.addRegistration(restApiRegistration);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return provider;
    }
}

@Configuration
class ApiRegistrationConfig {
    /** Log. */
    private static final Log log = LogFactory.getLog(ApiRegistrationConfig.class);

    @Autowired
    private Api connect;

    @Autowired
    private Api disconnect;

    @Autowired
    private Api get;

    @Autowired
    private Api update;

    @Bean
    public ApiRegistration restApiRegistration() {
        ApiRegistration registration = new ApiRegistration("REST", "1.0", "/authorize");
        log.debug("creating: " + registration.toString());

        try {
            registration.registerApi(connect);
            registration.registerApi(disconnect);
            registration.registerApi(get);
            registration.registerApi(update);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return registration;
    }
}

@Configuration
class ApiConfig {
    /** Log. */
    private static final Log log = LogFactory.getLog(ApiConfig.class);

    @Bean
    public Api connect() {
        Api api = new Api(
                "portal-session",
                "connect",
                "/sessions",
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public Api disconnect() {
        Api api = new Api(
                "portal-session",
                "disconnect",
                "/session",
                RequestMethod.DELETE.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public Api get() {
        Api api = new Api(
                "portal-session",
                "get-session",
                "/session",
                RequestMethod.GET.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public Api update() {
        Api api = new Api(
                "portal-session",
                "update-session",
                "/session",
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

}
