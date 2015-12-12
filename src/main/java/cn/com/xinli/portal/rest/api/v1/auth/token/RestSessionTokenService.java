package cn.com.xinli.portal.rest.api.v1.auth.token;

import cn.com.xinli.portal.rest.api.v1.SecureKeyGenerator;
import cn.com.xinli.portal.rest.api.v1.auth.challenge.Challenge;
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
public class RestSessionTokenService implements TokenService, InitializingBean {
    @Autowired
    private Cache sessionTokenCache;

    @Autowired
    private SecureKeyGenerator secureKeyGenerator;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(sessionTokenCache);
    }

    private Element createSessionTokenElement(RestSessionToken token) {
        long now = System.currentTimeMillis();
        return new Element(
                token.getKey(),
                token,
                CachingConfiguration.EHCACHE_VERSION,
                now,
                now,
                0,
                true,
                CachingConfiguration.SESSION_TOKEN_TTL,
                0,
                now);
    }

    @Override
    public Token allocateToken(String extendedInformation) {
        String key = secureKeyGenerator.generateUniqueRandomString();
        RestSessionToken token = new RestSessionToken(key, extendedInformation);
        Element element = sessionTokenCache.putIfAbsent(createSessionTokenElement(token));
        return (Token) element.getObjectValue();
    }

    @Override
    public Token verifyToken(String key) {
        Element element = sessionTokenCache.get(key);
        if (element != null) {
            Token issued = (Token) element.getObjectValue();
            if (issued.getKey().equals(key)) {
                return issued;
            }
        }
        return null;
    }

}
