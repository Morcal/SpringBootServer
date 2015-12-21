package cn.com.xinli.portal.rest.auth;

import cn.com.xinli.portal.rest.auth.challenge.Challenge;
import cn.com.xinli.portal.rest.token.AbstractToken;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class CacheKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        if (target != null) {
            if (target instanceof Challenge) {
                return Challenge.class.cast(target).getNonce();
            } else if (target instanceof AbstractToken) {
                return AbstractToken.class.cast(target).getKey();
            }
        }
        return null;
    }
}
