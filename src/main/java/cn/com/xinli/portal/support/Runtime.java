package cn.com.xinli.portal.support;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * System runtime.
 * @author zhoupeng, created on 2016/3/27.
 */
public class Runtime {
    public static class Session {
        private AtomicLong total;

        private Map<String, AtomicLong> mapping;

        public long add(String nas) {
            AtomicLong counter = mapping.get(nas);
            if (counter != null) {
                return counter.incrementAndGet();
            }
            return -1L;
        }

        public long descrease(String nas) {
            AtomicLong counter = mapping.get(nas);
            if (counter != null) {
                return counter.decrementAndGet();
            }
            return -1L;
        }
    }
}
