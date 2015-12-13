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
public class RestAccessTokenService extends AbstractRestTokenService {
    @Autowired
    private Ehcache accessTokenCache;

    @Override
    protected Ehcache getCache() {
        return accessTokenCache;
    }

    @Override
    protected int getTokenTtl() {
        return CachingConfiguration.ACCESS_TOKEN_TTL;
    }

    @Override
    protected Token createToken(String key, String extendedInformation) {
        return new RestAccessToken(key, extendedInformation);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(accessTokenCache);
    }
}
