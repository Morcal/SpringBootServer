package cn.com.xinli.portal.auth;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/5.
 */
public class NonceTest {

    @Test
    public void testExpiration() {
        String challenge = RandomStringUtils.random(8);
        ApplicationAuthorization auth = new ApplicationAuthorization("key", "s3cr3t");
        Nonce nonce = new Nonce(auth, challenge, 10);
        long now = System.currentTimeMillis(), willExpire = now + 20 * 1000L;
        Assert.assertTrue(nonce.expired());

        Nonce n2 = new Nonce(null, null, 10);
    }
}
