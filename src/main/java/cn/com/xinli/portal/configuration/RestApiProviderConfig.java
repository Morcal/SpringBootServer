package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.rest.api.RestApiProvider;
import cn.com.xinli.portal.rest.api.RestApi;
import cn.com.xinli.portal.rest.api.RestApiRegistration;
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
public class RestApiProviderConfig {
    //public static final String

    @Autowired
    private RestApiRegistration restRestApiRegistration;

    @Bean
    public RestApiProvider apiProvider() {
        RestApiProvider provider = new RestApiProvider("Xinli Software Technology ltd., co.");
        try {
            provider.addRegistration(restRestApiRegistration);
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return provider;
    }
}

@Configuration
class RestApiRegistrationConfig {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestApiRegistrationConfig.class);

    @Autowired
    private RestApi connect;

    @Autowired
    private RestApi disconnect;

    @Autowired
    private RestApi get;

    @Autowired
    private RestApi update;

    @Bean
    public RestApiRegistration restApiRegistration() {
        RestApiRegistration registration = new RestApiRegistration("REST", "1.0", "/authorize");
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
class RestApiConfig {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestApiConfig.class);

    @Bean
    public RestApi connect() {
        RestApi api = new RestApi(
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
    public RestApi disconnect() {
        RestApi api = new RestApi(
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
    public RestApi get() {
        RestApi api = new RestApi(
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
    public RestApi update() {
        RestApi api = new RestApi(
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
