package cn.com.xinli.portal.web.rest;

import cn.com.xinli.portal.web.auth.BadRestCredentialsException;
import cn.com.xinli.portal.web.auth.HttpDigestCredentials;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * REST request test.
 *
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class RestRequestTest {
    /** Log. */
    private final Logger logger = LoggerFactory.getLogger(RestRequestTest.class);

    static final String privateKey = "kYjzVBB8Y0ZFbxSWbWovY3uYSQ2pTgmZeNu2VS4cg";

    Map<String, String> parameters = new HashMap<>();

    @Before
    public void setup() {
        for (int i = 0; i < 10; i++) {
            parameters.put(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10));
        }
        logger.debug("Parameters: {}", parameters);
    }

    @Test
    public void testRestRequest() throws BadRestCredentialsException {
        RestRequestSupport request = new RestRequestSupport("GET", "/portal");
        request.setAuthParameter("nonce", "some-nonce")
                .setAuthParameter("response", "challenge-response")
                .setAuthParameter("client_id", "foo-bar")
                .setAuthParameter("signature_method", "HMAC-SHA1")
                .setAuthParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                .setAuthParameter("version", "1.0")
                .setParameter("username", "foo")
                .setParameter("password", "bar")
                .setParameter("user_ip", "192.168.3.25")
                .setParameter("user_mac", "");

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            request.setParameter(entry.getKey(), entry.getValue());
        }
//        parameters.forEach(request::setParameter);

        final String name = "东风不言";

        logger.debug("setting unicode characters: {}.", name);
        request.setParameter("account", name);

        request.sign(privateKey);

        Assert.assertFalse(request.getParameters().isEmpty());
        Assert.assertTrue(request.getParameter("account").equals(name));

        String credentials = request.getCredentials().getCredentials();
        Assert.assertTrue(credentials.endsWith("\""));
        Assert.assertTrue(credentials.startsWith(HttpDigestCredentials.SCHEME));
        logger.debug(credentials);

        HttpDigestCredentials digest = HttpDigestCredentials.of(credentials);

        Assert.assertNotNull(digest);
        logger.debug("digest: {}", digest);

        Assert.assertEquals(credentials, digest.getCredentials());

        logger.debug("request: {}", request);
    }
}
