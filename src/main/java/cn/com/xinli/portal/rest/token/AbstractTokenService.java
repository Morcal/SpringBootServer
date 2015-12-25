package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.rest.SecureRandomStringGenerator;
import cn.com.xinli.portal.rest.configuration.CachingConfiguration;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/13.
 */
public abstract class AbstractTokenService implements TokenManager, TokenService, InitializingBean {

    @Autowired
    private SecureRandomStringGenerator secureRandomStringGenerator;

    protected abstract Ehcache getCache();

    protected abstract int getTokenTtl();

    protected abstract Token createToken(String key, String extendedInformation);

    private Element createTokenElement(Token token) {
        long now = System.currentTimeMillis();
        return new Element(
                token.getKey(),
                token,
                CachingConfiguration.EHCACHE_VERSION,
                now,
                now,
                0,
                true,
                getTokenTtl(),
                0,
                now);
    }

    @Override
    public Token allocateToken(String extendedInformation) {
        String key = secureRandomStringGenerator.generateUniqueRandomString();
        Token token = createToken(key, extendedInformation);
        getCache().put(createTokenElement(token));
        return token;
    }

    @Override
    public Token verifyToken(String key) {
        Element element = getCache().get(key);
        if (element != null) {
            Token issued = (Token) element.getObjectValue();
            if (issued.getKey().equals(key)) {
                return issued;
            }
        }
        return null;
    }

    @Override
    public boolean revokeToken(Token token) {
        return getCache().remove(token.getKey());
    }
}