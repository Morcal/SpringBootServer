package cn.com.xinli.portal.rest.configuration;

import cn.com.xinli.portal.rest.RestException;
import cn.com.xinli.portal.rest.api.EntryPoint;
import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.api.Registration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.StringJoiner;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
public class RestApiConfiguration {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestApiConfiguration.class);

    public static final String API_TYPE = "REST";
    public static final String REST_API_VERSION = "v1.0";
    public static final String REST_API_SESSION = "session";
    public static final String REST_API_SESSIONS = "sessions";
    public static final String REST_API_FIND = "find";
    public static final String REST_API_AUTHORIZE = "authorize";

    @Value("${application}") private String application;

    private String url(String api) {
        StringJoiner joiner = new StringJoiner("/");
        joiner.add("/" + application)
                .add(REST_API_VERSION)
                .add(api);
        return joiner.toString();
    }

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
        Registration registration = new Registration(
                API_TYPE,
                REST_API_VERSION,
                "/" + application + "/" + REST_API_VERSION + "/" + REST_API_AUTHORIZE);
        log.debug("> Creating: " + registration.toString());

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
                "authorize",
                url("authorize"),
                RequestMethod.GET.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "connect",
                url("sessions"),
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "disconnect",
                url("session"),
                RequestMethod.DELETE.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint get() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "get-session",
                url("session"),
                RequestMethod.GET.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }

    @Bean
    public EntryPoint update() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "update-session",
                url("session"),
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }


    @Bean
    public EntryPoint find() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "find-session",
                url("sessions/find"),
                RequestMethod.POST.name(),
                "JSON"
        );
        log.debug("> Creating: " + api.toString());
        return api;
    }
}
