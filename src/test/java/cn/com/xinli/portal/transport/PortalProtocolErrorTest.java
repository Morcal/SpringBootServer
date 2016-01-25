package cn.com.xinli.portal.transport;

import org.junit.Assert;
import org.junit.Test;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/22.
 */
public class PortalProtocolErrorTest {
    @Test
    public void testPortalProtocolError() {
        PortalProtocolException ex = new AuthenticationException(ProtocolError.AUTHENTICATION_REJECTED, "need pin");

        ProtocolError error = ex.getProtocolError();
        Assert.assertNotNull(error);

        Assert.assertEquals(ProtocolError.AUTHENTICATION_REJECTED, error);

        Assert.assertTrue(error.isAuthenticationError());
        Assert.assertFalse(error.isChallengeError());
        Assert.assertFalse(error.isLogoutError());

        ProtocolError err = ProtocolError.of(0xa0f);
        Assert.assertNotNull(err);

        Assert.assertEquals(ProtocolError.CHALLENGE_REJECTED, err);

        Assert.assertFalse(err.isAuthenticationError());
        Assert.assertTrue(err.isChallengeError());
        Assert.assertFalse(err.isLogoutError());
    }
}
