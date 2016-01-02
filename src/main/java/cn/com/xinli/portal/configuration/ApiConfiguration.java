package cn.com.xinli.portal.configuration;

import cn.com.xinli.portal.Activity;
import cn.com.xinli.portal.rest.api.EntryPoint;
import cn.com.xinli.portal.rest.api.Provider;
import cn.com.xinli.portal.rest.api.Registration;
import cn.com.xinli.portal.rest.token.TokenScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.StringJoiner;

/**
 * PWS REST APIs configurations.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/6.
 */
@Configuration
public class ApiConfiguration {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ApiConfiguration.class);

    /** API path. */
    public static final String API_PATH = "api";

    public static final String API_TYPE = "REST";
    public static final String REST_API_VERSION = "v1.0";
    public static final String REST_API_SESSION = "session";
    public static final String REST_API_SESSIONS = "sessions";
    public static final String REST_API_FIND = "sessions/find";
    public static final String REST_API_AUTHORIZE = "authorize";

    @Value("${pws.root}") private String serverApplication;

    private String url(String api) {
        StringJoiner joiner = new StringJoiner("/");
        joiner.add("/" + serverApplication)
                .add(REST_API_VERSION)
                .add(api);
        return joiner.toString();
    }

    @Bean
    public Provider restApiProvider() {
        Provider provider = new Provider();
        provider.setVendor("Xinli Software Technology ltd., co.");
        provider.addRegistration(restApiRegistration());
        return provider;
    }

    private Registration restApiRegistration() {
        Registration registration = new Registration(API_TYPE, REST_API_VERSION);
        logger.debug("Creating: {}.", registration);

        registration.registerApi(authorize());
        registration.registerApi(connect());
        registration.registerApi(disconnect());
        registration.registerApi(get());
        registration.registerApi(update());
        registration.registerApi(find());

        return registration;
    }

    private EntryPoint authorize() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_ACCESS_TOKEN_SCOPE.alias(),
                Activity.Action.AUTHENTICATE.alias(),
                url(REST_API_AUTHORIZE),
                RequestMethod.GET.name(),
                "JSON",
                false);
        logger.debug("Creating: {}.", api);
        return api;
    }

    private EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.Action.CREATE_SESSION.alias(),
                url(REST_API_SESSIONS),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.debug("Creating: {}.", api);
        return api;
    }

    private EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.Action.DELETE_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.DELETE.name(),
                "JSON",
                true);
        logger.debug("Creating: {}.", api);
        return api;
    }

    private EntryPoint get() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.Action.GET_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.debug("Creating: {}.", api);
        return api;
    }

    private EntryPoint update() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.Action.UPDATE_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.debug("Creating: {}.", api);
        return api;
    }

    private EntryPoint find() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.Action.FIND_SESSION.alias(),
                url(REST_API_FIND),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.debug("Creating: {}.", api);
        return api;
    }
}
