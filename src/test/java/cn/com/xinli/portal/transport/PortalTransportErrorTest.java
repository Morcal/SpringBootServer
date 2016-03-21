package cn.com.xinli.portal.transport;

import org.junit.Assert;
import org.junit.Test;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/22.
 */
public class PortalTransportErrorTest {
    @Test
    public void testPortalProtocolError() {
        TransportException ex = new AuthenticationException(TransportError.AUTHENTICATION_REJECTED, "need pin");

        TransportError error = ex.getProtocolError();
        Assert.assertNotNull(error);

        Assert.assertEquals(TransportError.AUTHENTICATION_REJECTED, error);

        Assert.assertTrue(error.isAuthenticationError());
        Assert.assertFalse(error.isChallengeError());
        Assert.assertFalse(error.isLogoutError());

        TransportError err = TransportError.of(0xa0f);
        Assert.assertNotNull(err);

        Assert.assertEquals(TransportError.CHALLENGE_REJECTED, err);

        Assert.assertFalse(err.isAuthenticationError());
        Assert.assertTrue(err.isChallengeError());
        Assert.assertFalse(err.isLogoutError());
    }
}
