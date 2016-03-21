package cn.com.xinli.portal.support;

import cn.com.xinli.portal.core.credentials.Credentials;
import cn.com.xinli.portal.core.credentials.CredentialsModifier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/2.
 */
public class CredentialsModifierTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CredentialsModifierTest.class);

    final Credentials credentials = Credentials.of("foo", "bar", "192.168.3.26", "20-CF-30-BB-E9-AF");

    @Test
    public void testPrefixPostfix() {
        CredentialsModifier prefix = new CredentialsModifier();
        prefix.setPosition(CredentialsModifier.Position.HEAD);
        prefix.setTarget(CredentialsModifier.Target.USERNAME);
        prefix.setValue("xl");

        CredentialsModifier postfix = new CredentialsModifier();

        prefix.setPosition(CredentialsModifier.Position.TAIL);
        prefix.setTarget(CredentialsModifier.Target.USERNAME);
        prefix.setValue("@xinli.com.cn");

        Credentials prefixed = prefix.modify(credentials);
        Assert.assertNotNull(prefixed);

        Assert.assertEquals("xlfoo", prefixed.getUsername());

        Credentials postfixed = postfix.modify(credentials);
        Assert.assertNotNull(postfixed);

        Assert.assertEquals("foo@xinli.com.cn", postfixed.getUsername());

        Credentials modified1 = prefix.modify(postfix.modify(credentials));
        Credentials modified2 = postfix.modify(prefix.modify(credentials));
        logger.debug("postfix then prefix, {}", modified1);
        logger.debug("prefix then postfix, {}", modified2);

        Assert.assertEquals(modified1, modified2);
    }
}
