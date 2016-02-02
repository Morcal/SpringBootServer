package cn.com.xinli.portal.transport.huawei.nio;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class SerialNumTest {
    final Set<Integer> results = new HashSet<>();

    public void inc() {
        for (int i = 0; i < 10000; i++) {
            int num = DefaultPortalClient.nextSerialNum();
            if (!results.add(num)) {
                throw new RuntimeException("found redundant number.");
            }
        }
    }

    @Test
    public void testSerialNum() {
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i = 0; i < 15; i++) {
            service.submit(this::inc);
        }
    }
}
