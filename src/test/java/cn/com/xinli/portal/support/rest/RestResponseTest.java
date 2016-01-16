package cn.com.xinli.portal.support.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Rest Response Test.
 *
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/14.
 */
public class RestResponseTest {
    /** Log. */
    private final Logger logger = LoggerFactory.getLogger(RestResponseTest.class);

    final String ErrorResponse = "{ \"truncated\": false, \"created_at\": 12345, \"error\": \"invalid_grant\" }";

    final String SuccessResponse = "{ " +
            "\"authentication\": { " +
            "\"nonce\":\"fgu890fn1kBnRKbf\", \"challenge\":\"MKFldNAfpAJFjFf\", \"expires_in\":60" +
            "}, " +
            "\"authorization\":{" +
            "\"token\": \"gmZeNu2VS4cGovY3uYSQ2pB8Y0ZFABkSQ2pB8Y0ZFaBKlLkYjz\"," +
            "\"token_type\": \"Bearer\", \"expires_in\": 3600, \"expires_at\": 1318622958," +
            "\"refresh_token\": \"VBB8Y0ZFaBGmZeNu2VS4cGOvY3uYxSWbWovY3uYSQ\"," +
            "\"scope\": \"portal-rest-api\"" +
            "}," +
            "\"session\": { " +
            "\"id\": \"xinli000000201511232209001\", " +
            "\"keepalive\": true, " +
            "\"keepalive_interval\": 120, " +
            "\"authentication\": { " +
            "\"urls\": [ " +
            "{ " +
            "\"account_url\": \"https://api.xinli.com.cn/account/foobar\" " +
            "}] " +
            "}, " +
            "\"authorization\": { " +
            "\"session_timeout\": 1200, " +
            "\"input_packets\": 300, " +
            "\"output_packets\": 100, " +
            "\"input_rate\": 4096, " +
            "\"output_rate\": 512, " +
            "\"urls\": [ " +
            "{ " +
            "\"package_url\": \"https://api.xinli.com.cn/package/foobar\" " +
            "} " +
            "] " +
            "}, " +
            "\"accounting\": { " +
            "\"package\": \"time\", " +
            "\"starttime\": 1318622958, " +
            "\"urls\": [ " +
            "{ " +
            "\"accounting_url\": \"https://api.xinli.com.cn/accounting/foobar\" " +
            "} " +
            "] " +
            "} " +
            "} " +
            "}";

    final String partial = "{\n" +
            "  \"authorization\": null,\n" +
            "  \"authentication\": {\n" +
            "    \"nonce\": \"qkomtqmi2hnpq3kl69f8ri6ln9\",\n" +
            "    \"challenge\": \"rg5jijp8f1jrtl9as0hunn6b9u\",\n" +
            "    \"expires_at\": 0,\n" +
            "    \"expires_in\": 30\n" +
            "  },\n" +
            "  \"session\": null\n" +
            "}";

    @Test
    public void testPartial() throws IOException {
        RestResponse success = RestResponseParser.parse(partial);
        Assert.assertNotNull(success);
        logger.debug("result: {}", success);
        logger.debug("error created at: {}", success.createdAt());
        logger.debug("error created at: {}", success.truncated());

        Assert.assertTrue(success instanceof Success);
    }

    @Test
    public void testErrorResponse() throws IOException {
        RestResponse error = RestResponseParser.parse(ErrorResponse);
        Assert.assertNotNull(error);
        logger.debug("error result: {}", error);
        if (error instanceof RestError) {
            logger.debug("failure.");
        }

        logger.debug("error created at: {}", error.createdAt());

        Assert.assertTrue(error instanceof RestError);
    }

    @Test
    public void testSuccessResponse() throws IOException {
        RestResponse success = RestResponseParser.parse(SuccessResponse);
        Assert.assertNotNull(success);
        logger.debug("result: {}", success);

        Assert.assertTrue(success instanceof Success);
    }

    @Test
    public void testCreateResponse() throws JsonProcessingException {
        Success success = new Success();
        success.setCreatedAt(System.currentTimeMillis() / 1000L);
        success.setTruncated(false);
        success.setAuthorization(null);
        success.setAuthentication(null);

        logger.debug("success: {}", success);

        logger.debug("authentication: {}", success.getAuthentication());
        logger.debug("authorization: {}", success.getAuthorization());

        logger.debug("json: {}", new ObjectMapper().writeValueAsString(success));
    }
}
