package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.rest.RestException;
import cn.com.xinli.portal.rest.api.EntryPoint;
import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.api.Registration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
public class RestApiProviderConfiguration {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestApiProviderConfiguration.class);

    @Bean
    public Provider restApiProvider() {
        Provider provider = new Provider("Xinli Software Technology ltd., co.");
        try {
            provider.addRegistration(restApiRegistration());
        } catch (RestException e) {
            e.printStackTrace();
        }
        return provider;
    }

    @Bean
    public Registration restApiRegistration() {
        Registration registration = new Registration("REST", "1.0", "/authorize");
        log.debug("creating: " + registration.toString());

        try {
            registration.registerApi(authorize());
            registration.registerApi(connect());
            registration.registerApi(disconnect());
            registration.registerApi(get());
            registration.registerApi(update());
        } catch (RestException e) {
            e.printStackTrace();
        }

        return registration;
    }

    @Bean
    public EntryPoint authorize() {
        EntryPoint api = new EntryPoint(
                "portal-authorize",
                "api",
                RestSecurityConfiguration.REST_API_AUTHORIZE_URL,
                RequestMethod.GET.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "connect",
                RestSecurityConfiguration.REST_API_SESSIONS_URL,
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "disconnect",
                RestSecurityConfiguration.REST_API_SESSION_URL,
                RequestMethod.DELETE.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint get() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "get-session",
                RestSecurityConfiguration.REST_API_SESSION_URL,
                RequestMethod.GET.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint update() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "update-session",
                RestSecurityConfiguration.REST_API_SESSION_URL,
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("creating: " + api.toString());
        return api;
    }

}
