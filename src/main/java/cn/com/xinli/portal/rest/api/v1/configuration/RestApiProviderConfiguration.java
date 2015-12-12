package cn.com.xinli.portal.rest.api.v1.configuration;

import cn.com.xinli.portal.configuration.ConfigurationException;
import cn.com.xinli.portal.rest.api.RestApi;
import cn.com.xinli.portal.rest.api.RestApiProvider;
import cn.com.xinli.portal.rest.api.RestApiRegistration;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.ChallengeManager;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.EhCacheChallengeManager;
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
    public ChallengeManager challengeManager() {
        return new EhCacheChallengeManager();
    }

    @Bean
    public RestApiProvider restApiProvider() {
        RestApiProvider provider = new RestApiProvider("Xinli Software Technology ltd., co.");
        try {
            provider.addRegistration(restApiRegistration());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return provider;
    }

    @Bean
    public RestApiRegistration restApiRegistration() {
        RestApiRegistration registration = new RestApiRegistration("REST", "1.0", "/authorize");
        log.debug("creating: " + registration.toString());

        try {
            registration.registerApi(authorize());
            registration.registerApi(connect());
            registration.registerApi(disconnect());
            registration.registerApi(get());
            registration.registerApi(update());
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return registration;
    }

    @Bean
    public RestApi authorize() {
        RestApi api = new RestApi(
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
    public RestApi connect() {
        RestApi api = new RestApi(
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
    public RestApi disconnect() {
        RestApi api = new RestApi(
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
    public RestApi get() {
        RestApi api = new RestApi(
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
    public RestApi update() {
        RestApi api = new RestApi(
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
