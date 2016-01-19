package cn.com.xinli.portal.support.rest;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/19.
 */
public class RegistrationTest {
    @Test
    public void testRegistration() {
        Registration registration = new Registration();
        List<EntryPoint> apis = new ArrayList<>();
        registration.setApis(apis);

        Assert.assertNotNull(registration.getApis());
        Assert.assertTrue(registration.getApis().isEmpty());
    }
}
