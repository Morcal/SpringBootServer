package cn.com.xinli.portal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

/**
 * Message test.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/24.
 */
public class MessageTest extends TestBase {
    static final String OK = "ok";

    @Test
    public void testSuccessMessage() {
        Message<String> message = Message.of(OK, true, "result is okay.");

        Assert.assertNotNull(message);
        Assert.assertThat(true, Matchers.is(message.isSuccess()));
    }

    @Test
    public void testFailureMessage() {
        Message<String> message = Message.of(null, false, "result is failure.");

        Assert.assertNotNull(message);
        Assert.assertFalse(message.isSuccess());
    }
}
