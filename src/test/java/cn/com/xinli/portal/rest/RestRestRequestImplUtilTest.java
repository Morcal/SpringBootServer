package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.rest.api.v1.auth.HttpDigestCredentials;
import cn.com.xinli.portal.rest.api.v1.RestRequestImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: portal-rest-api
 *
 * @author zhoupeng 2015/12/9.
 */
public class RestRestRequestImplUtilTest {
    /** Log. */
    private static final Log log = LogFactory.getLog(RestRestRequestImplUtilTest.class);

    static final String secretKey = "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg";

    Map<String, String> parameters = new HashMap<>();

    @Before
    public void setup() {
        for (int i = 0; i < 10; i++) {
            parameters.put(RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10));
        }
        log.debug(parameters);
    }

    @Test
    public void testRestRequest() {
        RestRequestImpl.Builder builder = RestRequestImpl.builder().setUrl("http://www.baidu.com")
                .setMethod("GET")
                .setAuthParam("nonce", "some-nonce")
                .setAuthParam("response", "challenge-response")
                .setAuthParam("client_id", "foo-bar")
                .setAuthParam("signature_method", "HMAC-SHA1")
                .setAuthParam("timestamp", String.valueOf(System.currentTimeMillis()))
                .setAuthParam("version", "1.0")
                .setParameter("username", "foo")
                .setParameter("password", "bar")
                .setParameter("user_ip", "192.168.3.25")
                .setParameter("user_mac", "");

        parameters.forEach(builder::setParameter);

        builder.setParameter("account", "东风不言");

        RestRequestImpl restRequestImpl = builder.build();

        Assert.assertNotNull(restRequestImpl);

        restRequestImpl.sign(secretKey);

        String credentials = restRequestImpl.getCredentials();
        Assert.assertThat(credentials, CoreMatchers.endsWith("\""));
        Assert.assertThat(credentials, CoreMatchers.startsWith(HttpDigestCredentials.SCHEME));

        log.debug(restRequestImpl);
    }
}
