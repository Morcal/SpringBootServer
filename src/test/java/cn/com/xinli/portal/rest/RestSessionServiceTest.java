package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PortalApplication.class)
@DirtiesContext
public class RestSessionServiceTest extends SpringBootTestBase {
    @Autowired
    private RestSessionService restSessionService;

    @Test
    public void testCreateSession() throws PortalException {
        final String ip = "192.168.3.25",
                mac = "f4:6d:04:53:a0:f0",
                nasId = "nas-01";

        Session session = restSessionService.createSession(ip, mac, nasId);
        Assert.assertNotNull(session);
        log.debug(session);

        session = restSessionService.createSession(ip, mac, nasId);
        Assert.assertNotNull(session);
        log.debug(session);
    }
}
