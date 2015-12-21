package cn.com.xinli.portal.rest;

import cn.com.xinli.portal.*;
import cn.com.xinli.portal.persist.SessionEntity;
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
public class SessionServiceSupportTest extends SpringBootTestBase {
    @Autowired
    private SessionService sessionService;

    @Test
    public void testCreateSession() throws PortalException {
        final String ip = "192.168.3.25",
                mac = "f4:6d:04:53:a0:f0",
                os = "iOS",
                version = "1.0",
                username = "foo",
                password = "bar",
                nasId = "01";

        SessionEntity entity = new SessionEntity();
        entity.setIp(ip);
        entity.setMac(mac);
        entity.setOs(os);
        entity.setVersion(version);
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setNasId(nasId);

        Session session = sessionService.createSession(entity);
        Assert.assertNotNull(session);
        log.debug(session);

        session = sessionService.createSession(entity);
        Assert.assertNotNull(session);
        log.debug(session);
    }
}
