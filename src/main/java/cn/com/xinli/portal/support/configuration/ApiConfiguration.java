package cn.com.xinli.portal.support.configuration;

import cn.com.xinli.portal.core.activity.Activity;
import cn.com.xinli.portal.web.auth.token.TokenScope;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.web.rest.EntryPoint;
import cn.com.xinli.portal.web.rest.Provider;
import cn.com.xinli.portal.web.rest.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.StringJoiner;

/**
 * PWS REST APIs configurations.
 *
 * <p>REST APIs' structure was defines as:
 * <pre>
 *     {
 *         "vendor": "",
 *         "registrations": [{
 *              "type":"REST",
 *              "version":"v1.0",
 *              "apis":[{
 *                  "scope":"portal-rest-api",
 *                  "action":"authorize",
 *                  "url":"/portal/v1.0/authorize",
 *                  "method":"GET",
 *                  "response":"JSON",
 *                  "requires_auth":false
 *              },
 *              ...
 *         }]
 *     }
 * </pre>
 *
 * <p>Project: xpws
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

    /**
     * Create portal api end point url.
     * @param api api end point.
     * @return url.
     */
    private String url(String api) {
        StringJoiner joiner = new StringJoiner("/");
        joiner.add("/portal")
                .add(REST_API_VERSION)
                .add(api);
        return joiner.toString();
    }

    /**
     * Define portal REST api provider.
     *
     * @return portal REST api provider.
     * @throws ServerException
     */
    @Bean
    public Provider restApiProvider() throws ServerException {
        Provider provider = new Provider();
        provider.setVendor("Xinli Software Technology ltd., co.");
        provider.addRegistration(restApiRegistration());
        return provider;
    }

    /**
     * Define REST api registration.
     * @return REST api registration.
     * @throws ServerException
     */
    private Registration restApiRegistration() throws ServerException {
        Registration registration = new Registration(API_TYPE, REST_API_VERSION);
        logger.info("Creating: {}.", registration);

        registration.registerApi(authorize());
        registration.registerApi(connect());
        registration.registerApi(disconnect());
        registration.registerApi(get());
        registration.registerApi(update());
        registration.registerApi(find());

        return registration;
    }

    /**
     * Define authorize end point.
     * @return authorize end point.
     */
    private EntryPoint authorize() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_ACCESS_TOKEN_SCOPE.alias(),
                Activity.SessionAction.AUTHENTICATE.alias(),
                url(REST_API_AUTHORIZE),
                RequestMethod.GET.name(),
                "JSON",
                false);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define connect end point.
     * @return connect end point.
     */
    private EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.CREATE_SESSION.alias(),
                url(REST_API_SESSIONS),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define disconnect end point.
     * @return disconnect end point.
     */
    private EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.DELETE_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.DELETE.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define get end point.
     * @return get end point.
     */
    private EntryPoint get() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.GET_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define update end point.
     * @return update end point.
     */
    private EntryPoint update() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.UPDATE_SESSION.alias(),
                url(REST_API_SESSION),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define find end point.
     * @return find end point.
     */
    private EntryPoint find() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.FIND_SESSION.alias(),
                url(REST_API_FIND),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }
}
