package cn.com.xinli.portal.auth;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HTTP digest credentials test.
 *
 * Project: rest-api-rest-api
 *
 * @author zhoupeng 2015/12/28.
 */
public class HttpDigestCredentialsTest {
    /** Log. */
    private final Logger logger = LoggerFactory.getLogger(HttpDigestCredentialsTest.class);

    final String[] BadCredentials = {
            "Digest foo=\"bar\"",
            /* Bad form string ends with the delimiter. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\", ",
            /* Bad form string ends with the delimiter and an additional space. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\",  ",
    };

    final String[] GoodCredentials = {
            "Digest client_id=\"jportal\", nonce=\"some-nonce\", response=\"any-response\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
            /* Bad timestamp, but credential doesn't take care of content type. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"abcdef\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
            /* Bad form string missing a single space between client_id and client_token.
             * But it's a valid credential without client_token attribute and value,
             * because it's in the client_id attribute value, which means attirbute
             * client_id is name: "client_id" value: "jportal",client_token="some-client-token".
             * So, this credential will fail access token testings.
             */
            "Digest client_id=\"jportal\",client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
    };

    final String[] BadAccessTokenCredentials = {
            /* Remember this testing string passed the credential testing.
             * Bad form string missing a single space between client_id and client_token. */
            "Digest client_id=\"jportal\",client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
    };

    final String[] GoodAccessTokenCredentials = {
            /* Access token with session token. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
            /* Access token without session token. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\"",
    };

    final String[] BadSessionTokenCredentials = {
            /* Missing session token. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\"",

    };

    final String[] GoodSessionTokenCredentials = {
            /* Session token with client access token. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
            /* Session token with challenge response and client_token(shouldn't happen but legit). */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", " +
                    "nonce=\"some-nonce\", response=\"any-response\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
            /* session token without client id and access token. */
            "Digest client_id=\"jportal\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
    };

    final String[] BadChallengeCredentials = {
            "Digest foo=\"bar\"",
            /* A bad challenge credentials missing response. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", " +
                    "nonce=\"some-nonce\", signature=\"signed\"," +
                    " signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\"",
    };

    final String[] GoodChallengeCredentials = {
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", " +
                    "nonce=\"some-nonce\", response=\"any-response\", signature=\"signed\"," +
                    " signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\"",
    };

    final String[] GoodAccessAndSessionTokenCredentials = {
            /* Session token with client access token. */
            "Digest client_id=\"jportal\", client_token=\"some-client-token\", signature=\"signed\", " +
                    "signature_method=\"HMAC-SHA1\", timestamp=\"12345678\", version=\"1.0\", " +
                    "session_token=\"some-session-token\"",
    };

    @Test
    public void testBadCredentials() {
        int i = 0, len = BadCredentials.length;
        for (String credentials : BadCredentials) {
            try {
                HttpDigestCredentials.of(credentials);
            } catch (Exception e) {
                i++;
                logger.debug("Bad credentials: {}", credentials);
            }
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testGoodCredentials() {
        int i = 0, len = GoodCredentials.length;
        for (String credentials : GoodCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertNotNull(cred);
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testBadAccessTokens() {
        int i = 0, len = BadAccessTokenCredentials.length;
        for (String credentials : BadAccessTokenCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertNotNull(cred);
            Assert.assertFalse(HttpDigestCredentials.containsAccessToken(cred));
            i++;
            logger.debug("Bad access token: {}", credentials);
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testGoodAccessTokens() {
        int i = 0, len = GoodAccessTokenCredentials.length;
        for (String credentials : GoodAccessTokenCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertNotNull(cred);
            Assert.assertTrue(HttpDigestCredentials.containsAccessToken(cred));
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testGoodSessionTokens() {
        int i = 0, len = GoodSessionTokenCredentials.length;
        for (String credentials : GoodSessionTokenCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertTrue(HttpDigestCredentials.containsSessionToken(cred));
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testBadSessionTokens() {
        int i = 0, len = BadSessionTokenCredentials.length;
        for (String credentials : BadSessionTokenCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertFalse(HttpDigestCredentials.containsSessionToken(cred));
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testGoodChallenges() {
        int i = 0, len = GoodChallengeCredentials.length;
        for (String credentials : GoodChallengeCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertTrue(HttpDigestCredentials.containsChallenge(cred));
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testBadChallenges() {
        int i = 0, len = BadChallengeCredentials.length;
        for (String credentials : BadChallengeCredentials) {
            try {
                HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
                Assert.assertFalse(HttpDigestCredentials.containsChallenge(cred));
                i++;
            } catch (Exception e) {
                i++;
                logger.debug("Bad challenge credentials: {}", credentials);
            }
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testGoodAccessAndSessionTokens() {
        int i = 0, len = GoodAccessAndSessionTokenCredentials.length;
        for (String credentials : GoodAccessAndSessionTokenCredentials) {
            HttpDigestCredentials cred = HttpDigestCredentials.of(credentials);
            Assert.assertTrue(HttpDigestCredentials.containsSessionToken(cred));
            Assert.assertTrue(HttpDigestCredentials.containsAccessToken(cred));
            Assert.assertNotEquals(0, cred.getParameters().size());
            i++;
        }

        Assert.assertEquals(len, i);
    }

    @Test
    public void testCopyCredentials() {
        HttpDigestCredentials cred = HttpDigestCredentials.of(GoodAccessAndSessionTokenCredentials[0]);
        Assert.assertNotNull(cred);
        Assert.assertTrue(HttpDigestCredentials.containsSessionToken(cred));
        Assert.assertTrue(HttpDigestCredentials.containsAccessToken(cred));

        HttpDigestCredentials copy = new HttpDigestCredentials();
        copy.copy(cred);

        Assert.assertNotNull(copy);
        Assert.assertTrue(HttpDigestCredentials.containsSessionToken(copy));
        Assert.assertTrue(HttpDigestCredentials.containsAccessToken(copy));

        Assert.assertEquals(copy.toString(), cred.toString());
    }

    @Test
    public void testHeadName() {
        Assert.assertFalse(StringUtils.isEmpty(HttpDigestCredentials.HEADER_NAME));
    }
}
