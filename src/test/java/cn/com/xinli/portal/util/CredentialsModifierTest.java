package cn.com.xinli.portal.util;

import cn.com.xinli.portal.protocol.Credentials;
import cn.com.xinli.portal.protocol.CredentialsModifier;
import cn.com.xinli.portal.protocol.support.PrefixPostfixCredentialsModifier;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/2.
 */
public class CredentialsModifierTest {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(CredentialsModifierTest.class);

    final Credentials credentials = new Credentials("foo", "bar", "192.168.3.26", "20-CF-30-BB-E9-AF");

    @Test
    public void testPrefixPostfix() {

        PrefixPostfixCredentialsModifier prefix = new PrefixPostfixCredentialsModifier(
                CredentialsModifier.Target.USERNAME,
                CredentialsModifier.Position.HEAD,
                "xl");

        PrefixPostfixCredentialsModifier postfix = new PrefixPostfixCredentialsModifier(
                CredentialsModifier.Target.USERNAME,
                CredentialsModifier.Position.TAIL,
                "@xinli.com.cn");

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
