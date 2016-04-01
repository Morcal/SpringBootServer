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
    public static final String API_PATH = "/portal/api";
    public static final String ADMIN_API_PATH = "/portal/admin/api";
    public static final String API_TYPE = "REST";
    public static final String ADMIN_API_TYPE = "ADMIN";
    public static final String REST_API_VERSION = "v1.0";
    public static final String REST_API_SESSION = "sessions";
    public static final String REST_API_FIND = "sessions/find";
    public static final String REST_API_AUTHORIZE = "authorize";
    public static final String REST_ADMIN_API_AUTHORIZE = "authorize";
    public static final String REST_ADMIN_API_LOGIN = "login";
    public static final String ADMIN_API_VERSION = "v1.0";
    public static final String ADMIN_API_NAS = "nas";
    public static final String ADMIN_API_SEARCH_NAS = "search/nas";
    public static final String ADMIN_API_TRANSLATION = "translations";
    public static final String ADMIN_API_MODIFIER = "modifiers";
    public static final String ADMIN_API_ACTIVITY = "activities";
    public static final String ADMIN_API_CONFIGURE = "configuration";
    public static final String ADMIN_API_CERTIFICATE = "certificates";
    public static final String ADMIN_API_SEARCH_CERTIFICATE = "search/certificates";
    public static final String ADMIN_API_STATISTICS = "statistics";

    /**
     * Create portal api entry point url.
     * @param root api root path.
     * @param version api version.
     * @param api api entry point.
     * @return url.
     */
    private String url(String root, String version, String api) {
        StringJoiner joiner = new StringJoiner("/");
        joiner.add(root)
                .add(version)
                .add(api);
        return joiner.toString();
    }

    /**
     * Define portal REST api provider.
     *
     * @return portal REST api provider.
     * @throws ServerException
     */
    @Bean(name = "rest-api-provider")
    public Provider restApiProvider() throws ServerException {
        Provider provider = new Provider();
        provider.setVendor("Xinli Software Technology ltd., co.");
        provider.addRegistration(restApiRegistration());
        return provider;
    }

    /**
     * Define portal REST admin api provider.
     *
     * @return portal REST admin api provider.
     * @throws ServerException
     */
    @Bean(name = "admin-api-provider")
    public Provider adminRestApiProvider() throws ServerException {
        Provider provider = new Provider();
        provider.setVendor("Xinli Software Technology ltd., co.");
        provider.addRegistration(adminRestApiRegistration());
        return provider;
    }

    /**
     * Define REST api registration.
     * @return REST api registration.
     * @throws ServerException
     */
    private Registration adminRestApiRegistration() throws ServerException {
        Registration registration = new Registration(ADMIN_API_TYPE, ADMIN_API_VERSION);
        logger.info("Creating: {}.", registration);

        registration.registerApi(adminLogin());
        registration.registerApi(adminAuthorize());
        registration.registerApi(findSession());
        registration.registerApi(deleteSession());
        registration.registerApi(searchNas());
        registration.registerApi(getNas());
        registration.registerApi(createNas());
        registration.registerApi(updateNas());
        registration.registerApi(getConfiguration());
        registration.registerApi(configureSystem());
        registration.registerApi(searchActivity());
        registration.registerApi(getActivity());
        registration.registerApi(searchCertificates());
        registration.registerApi(getCertificates());
        registration.registerApi(createCertificate());
        registration.registerApi(updateCertificate());
        registration.registerApi(getSystemStatistics());

        return registration;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint adminLogin() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.AdminAction.LOGIN.alias(),
                url("/portal/admin", ADMIN_API_VERSION, REST_ADMIN_API_LOGIN),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint adminAuthorize() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.AdminAction.AUTHORIZE.alias(),
                url("/portal/admin", ADMIN_API_VERSION, REST_ADMIN_API_AUTHORIZE),
                RequestMethod.GET.name(),
                "JSON",
                false);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create configure system api entry point.
     * @return api entry point.
     */
    private EntryPoint getConfiguration() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.AdminAction.GET_CONFIG.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_CONFIGURE),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create configure system api entry point.
     * @return api entry point.
     */
    private EntryPoint configureSystem() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.AdminAction.CONFIGURE.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_CONFIGURE),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint findSession() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.SessionAction.FIND_SESSION.alias(),
                url("/portal/admin", ADMIN_API_VERSION, REST_API_SESSION),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint deleteSession() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.SessionAction.DELETE_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_SESSION),
                RequestMethod.DELETE.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint getNas() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.NasAction.GET.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_NAS),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create api entry for adding NAS device.
     * @return api entry point.
     */
    private EntryPoint createNas() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.NasAction.ADD.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_NAS),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create api entry for updating NAS device.
     * @return api entry point.
     */
    private EntryPoint updateNas() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.NasAction.UPDATE.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_NAS),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define get nas entry point.
     * @return authorize entry point.
     */
    private EntryPoint searchNas() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.NasAction.SEARCH.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_SEARCH_NAS),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint searchActivity() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.ActivityAction.SEARCH.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_ACTIVITY),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create get activity api entry point.
     * @return api entry point.
     */
    private EntryPoint getActivity() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.ActivityAction.GET.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_ACTIVITY),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create search certificates api entry point.
     * @return api entry point.
     */
    private EntryPoint searchCertificates() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.CertificateAction.SEARCH.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_SEARCH_CERTIFICATE),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create get certificate information api entry point.
     * @return api entry point.
     */
    private EntryPoint getCertificates() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.CertificateAction.GET.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_CERTIFICATE),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create api entry for creating certificate.
     * @return api entry point.
     */
    private EntryPoint createCertificate() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.CertificateAction.CREATE.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_CERTIFICATE),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create api entry for updating certificate.
     * @return api entry point.
     */
    private EntryPoint updateCertificate() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.CertificateAction.UPDATE.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_CERTIFICATE),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Create get system statistics api entry point.
     * @return api entry point.
     */
    private EntryPoint getSystemStatistics() {
        EntryPoint api = new EntryPoint(
                TokenScope.SYSTEM_ADMIN_TOKEN_SCOPE.alias(),
                Activity.SystemAction.GET_SYSTEM_STATISTICS.alias(),
                url("/portal/admin", ADMIN_API_VERSION, ADMIN_API_STATISTICS),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
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
     * Define authorize entry point.
     * @return authorize entry point.
     */
    private EntryPoint authorize() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_ACCESS_TOKEN_SCOPE.alias(),
                Activity.SessionAction.AUTHENTICATE.alias(),
                url("/portal", REST_API_VERSION, REST_API_AUTHORIZE),
                RequestMethod.GET.name(),
                "JSON",
                false);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define connect entry point.
     * @return connect entry point.
     */
    private EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.CREATE_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_SESSION),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define disconnect entry point.
     * @return disconnect entry point.
     */
    private EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.DELETE_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_SESSION),
                RequestMethod.DELETE.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define get entry point.
     * @return get entry point.
     */
    private EntryPoint get() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.GET_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_SESSION),
                RequestMethod.GET.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define update entry point.
     * @return update entry point.
     */
    private EntryPoint update() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.UPDATE_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_SESSION),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }

    /**
     * Define find entry point.
     * @return find entry point.
     */
    private EntryPoint find() {
        EntryPoint api = new EntryPoint(
                TokenScope.PORTAL_SESSION_TOKEN_SCOPE.alias(),
                Activity.SessionAction.FIND_SESSION.alias(),
                url("/portal", REST_API_VERSION, REST_API_FIND),
                RequestMethod.POST.name(),
                "JSON",
                true);
        logger.info("Creating: {}.", api);
        return api;
    }
}
