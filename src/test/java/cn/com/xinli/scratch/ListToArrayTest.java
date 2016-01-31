package cn.com.xinli.scratch;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/2.
 */
public class ListToArrayTest {

    @Test
    public void testListToArray() {
        List<String> list = new ArrayList<>();
        list.add("abc");
        list.add("def");
        String[] strings = list.toArray(new String[list.size()]);
        Assert.assertEquals(2, strings.length);
    }
}
