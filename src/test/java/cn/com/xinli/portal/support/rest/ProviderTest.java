package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * REST API provider test.
 * 
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/29.
 */
public class ProviderTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ProviderTest.class);

    public static final String API_TYPE = "REST";
    public static final String REST_API_VERSION = "v1.0";
    public static final String REST_API_SESSION = "session";
    public static final String REST_API_SESSIONS = "sessions";
    public static final String REST_API_FIND = "sessions/find";
    public static final String REST_API_AUTHORIZE = "authorize";

    Provider provider;
    @Before
    public void setup() {
        provider = restApiProvider();
    }

    @Test
    public void testFindApi() {
        Assert.assertFalse(provider.getRegistrations().isEmpty());

        Assert.assertFalse(StringUtils.isEmpty(provider.getVendor()));

        Optional<EntryPoint> entryPoint = provider.findApi("POST", "/portal/v1.0/sessions");
        Assert.assertTrue(entryPoint.isPresent());

        entryPoint = provider.findApi("GET", "/portal/v1.0/sessions");
        Assert.assertFalse(entryPoint.isPresent());

        entryPoint = provider.findApi("POST", "/portal/v1.0/sessions");
        Assert.assertTrue(entryPoint.isPresent());

        entryPoint = provider.findApi("DELETE", "/portal/v1.0/session");
        Assert.assertTrue(entryPoint.isPresent());

        entryPoint = provider.findApi("DELETE", "/portal/v1.0/sessions");
        Assert.assertFalse(entryPoint.isPresent());

        entryPoint = provider.findApi("POST", "/portal/v1.0/sessions/find");
        Assert.assertTrue(entryPoint.isPresent());

        entryPoint = provider.findApi("GET", "/portal/v1.0/sessions/find");
        Assert.assertFalse(entryPoint.isPresent());
    }

    @Test
    public void testSetRegistration() {
        provider.setRegistrations(Collections.<Registration>emptyList());
//        provider.setRegistrations(Collections.emptyList());
        Assert.assertTrue(provider.getRegistrations().isEmpty());
    }

    @Test
    public void testParseApiProvider() throws IOException {
        final String json = "{\"vendor\":\"Xinli Software Technology ltd., co.\"," +
                "\"registrations\":[{\"type\":\"REST\",\"version\":\"v1.0\"," +
                "\"apis\":[" +
                "{\"scope\":\"portal-authorize\",\"action\":\"authorize\",\"url\":\"/portal/v1.0/authorize\",\"method\":\"GET\",\"response\":\"JSON\",\"requires_auth\":false}," +
                "{\"scope\":\"portal-session\",\"action\":\"connect\",\"url\":\"/portal/v1.0/sessions\",\"method\":\"POST\",\"response\":\"JSON\",\"requires_auth\":true}," +
                "{\"scope\":\"portal-session\",\"action\":\"disconnect\",\"url\":\"/portal/v1.0/session\",\"method\":\"DELETE\",\"response\":\"JSON\",\"requires_auth\":true}," +
                "{\"scope\":\"portal-session\",\"action\":\"get-session\",\"url\":\"/portal/v1.0/session\",\"method\":\"GET\",\"response\":\"JSON\",\"requires_auth\":true}," +
                "{\"scope\":\"portal-session\",\"action\":\"update-session\",\"url\":\"/portal/v1.0/session\",\"method\":\"POST\",\"response\":\"JSON\",\"requires_auth\":true}," +
                "{\"scope\":\"portal-session\",\"action\":\"find-session\",\"url\":\"/portal/v1.0/sessions/find\",\"method\":\"POST\",\"response\":\"JSON\",\"requires_auth\":true}]}]}";
        ObjectMapper mapper = new ObjectMapper();
        Provider provider = mapper.readValue(json, Provider.class);
        Assert.assertNotNull(provider);
        Assert.assertFalse(provider.getRegistrations().isEmpty());

        Optional<EntryPoint> entryPoint = provider.findApi("POST", "/portal/v1.0/sessions");
        Assert.assertTrue(entryPoint.isPresent());
    }

    @Test
    public void testEntryPoint() {
        EntryPoint entryPoint = new EntryPoint();
        EntryPoint entryPoint2 = new EntryPoint();
        Assert.assertTrue(entryPoint.equals(entryPoint2));
    }

    private String url(String api) {
        List<String> joiner = new ArrayList<>();
        joiner.add("/portal");
        joiner.add(REST_API_VERSION);
        joiner.add(api);

        return StringUtils.join(joiner, "/");
//        StringJoiner joiner = new StringJoiner("/");
//        joiner.add("/portal")
//                .add(REST_API_VERSION)
//                .add(api);
//        return joiner.toString();
    }

    public Provider restApiProvider() {
        Provider provider = new Provider();
        provider.setVendor("Xinli Software Technology ltd., co.");
        provider.addRegistration(restApiRegistration());
        return provider;
    }


    public Registration restApiRegistration() {
        Registration registration = new Registration(API_TYPE, REST_API_VERSION);
        logger.debug("> Creating: {}", registration);

        registration.registerApi(authorize());
        registration.registerApi(connect());
        registration.registerApi(disconnect());
        registration.registerApi(get());
        registration.registerApi(update());
        registration.registerApi(find());

        return registration;
    }


    public EntryPoint authorize() {
        EntryPoint api = new EntryPoint(
                "portal-authorize",
                "authorize",
                url(REST_API_AUTHORIZE),
                "GET",
                "JSON",
                false);
        logger.debug("> Creating: {}", api);
        return api;
    }


    public EntryPoint connect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "connect",
                url(REST_API_SESSIONS),
                "POST",
                "JSON",
                true);
        logger.debug("> Creating: {}", api);
        return api;
    }


    public EntryPoint disconnect() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "disconnect",
                url(REST_API_SESSION),
                "DELETE",
                "JSON",
                true);
        logger.debug("> Creating: {}", api);
        return api;
    }


    public EntryPoint get() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "get-session",
                url(REST_API_SESSION),
                "GET",
                "JSON",
                true);
        logger.debug("> Creating: {}", api);
        return api;
    }


    public EntryPoint update() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "update-session",
                url(REST_API_SESSION),
                "POST",
                "JSON",
                true);
        logger.debug("> Creating: {}", api);
        return api;
    }



    public EntryPoint find() {
        EntryPoint api = new EntryPoint(
                "portal-session",
                "find-session",
                url(REST_API_FIND),
                "POST",
                "JSON",
                true);
        logger.debug("> Creating: {}", api);
        return api;
    }
}
