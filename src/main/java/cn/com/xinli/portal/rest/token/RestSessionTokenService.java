package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Service
public class RestSessionTokenService extends AbstractRestTokenService {
    @Autowired
    private Ehcache sessionTokenCache;

    @Override
    protected Ehcache getCache() {
        return sessionTokenCache;
    }

    @Override
    protected int getTokenTtl() {
        return CachingConfiguration.SESSION_TOKEN_TTL;
    }

    @Override
    protected Token createToken(String key, String extendedInformation) {
        return new RestSessionToken(key, extendedInformation);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionTokenCache);
    }
}
