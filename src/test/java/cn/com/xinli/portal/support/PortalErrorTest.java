package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.PortalError;
import org.junit.Assert;
import org.junit.Test;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/23.
 */
public class PortalErrorTest {
    @Test
    public void testPortalError() {
        final PortalError srv = PortalError.SERVER_INTERNAL_ERROR,
                client = PortalError.INVALID_CLIENT,
                quota = PortalError.QUOTA_EXCEEDED;

        Assert.assertEquals(true, srv.isSystemError());
        Assert.assertEquals(true, client.isRestError());
        Assert.assertEquals(true, quota.isServiceError());
    }
}
