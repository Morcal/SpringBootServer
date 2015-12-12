package cn.com.xinli.portal.rest.api.v1.auth.token;

import cn.com.xinli.portal.rest.api.v1.SecureKeyGenerator;
import cn.com.xinli.portal.rest.api.v1.configuration.CachingConfiguration;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/11.
 */
@Service
public class RestAccessTokenService implements TokenService, InitializingBean {
    @Autowired
    private Cache accessTokenCache;

    @Autowired
    private SecureKeyGenerator secureKeyGenerator;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(accessTokenCache);
        Assert.notNull(secureKeyGenerator);
    }

    private Element createSessionTokenElement(RestAccessToken token) {
        long now = System.currentTimeMillis();
        return new Element(
                token.getKey(),
                token,
                CachingConfiguration.EHCACHE_VERSION,
                now,
                now,
                0,
                true,
                CachingConfiguration.ACCESS_TOKEN_TTL,
                0,
                now);
    }

    @Override
    public Token allocateToken(String extendedInformation) {
        String key = secureKeyGenerator.generateUniqueRandomString();
        RestAccessToken token = new RestAccessToken(key, extendedInformation);
        Element element = accessTokenCache.putIfAbsent(createSessionTokenElement(token));
        return (Token) element.getObjectValue();
    }

    @Override
    public Token verifyToken(String key) {
        Element element = accessTokenCache.get(key);
        if (element != null) {
            Token issued = (Token) element.getObjectValue();
            Assert.notNull(issued);
            if (issued.getKey().equals(key)) {
                return issued;
            }
        }
        return null;
    }
}
