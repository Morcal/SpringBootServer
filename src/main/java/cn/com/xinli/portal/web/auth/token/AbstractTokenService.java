package cn.com.xinli.portal.web.auth.token;

import cn.com.xinli.portal.core.Serializer;
import cn.com.xinli.portal.core.ServerException;
import cn.com.xinli.portal.core.configuration.ServerConfiguration;
import cn.com.xinli.portal.util.DigestUtils;
import cn.com.xinli.portal.web.configuration.SecurityConfiguration;
import cn.com.xinli.portal.web.util.SecureRandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Abstract Token Service
 *
 * <p>This class provides an abstraction of how to create token
 * and how to verify them.
 *
 * <p>Subclasses of this class need to provide token scope via
 * {@link #getTokenScope()}, token ttl via {@link #getTokenTtl()}
 * and token verification method via {@link #verifyExtendedInformation(String)}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
@Service
public abstract class AbstractTokenService implements TokenService, InitializingBean {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractTokenService.class);

    @Autowired
    private SecureRandomStringGenerator secureRandomStringGenerator;

    @Autowired
    private ServerConfiguration serverConfiguration;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(secureRandomStringGenerator);
    }

    /**
     * Get token scope.
     * @return token scope.
     */
    protected abstract TokenScope getTokenScope();

    /**
     * Get token serializer.
     * @return token serializer.
     */
    protected abstract Serializer<TokenKey> getTokenKeySerializer();

    /**
     * Verify extended information in token.
     * @param extendedInformation extended information in token.
     * @return true if extended information in token is valid.
     */
    protected abstract boolean verifyExtendedInformation(String extendedInformation);

    /**
     * Get token time to live (in seconds).
     * @return token ttl.
     */
    protected abstract int getTokenTtl();

    /**
     * Create SHA summary.
     * @param key token key.
     * @return SHA summary.
     */
    private String sha(TokenKey key) throws ServerException {
        return DigestUtils.sha256Hex(key.getContent() + serverConfiguration.getPrivateKey());
    }

    @Override
    public final Token verifyToken(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        final byte[] value = Base64.decode(Utf8.encode(key));
        /* Verify token key. */
        TokenKey tokenKey = getTokenKeySerializer().deserialize(value);
        if (tokenKey == null) {
            return null;
        }

        /* Verify scope. */
        if (tokenKey.getScope() != getTokenScope()) {
            logger.debug("Invalid token scope.");
            return null;
        }

        /* Verify expiration. */
        if ((System.currentTimeMillis() - tokenKey.getCreationTime()) / 1000L > getTokenTtl()) {
            logger.debug("Token expired.");
            return null;
        }

        /* Verify digest. */
        try {
            String digest = sha(tokenKey);

            if (!tokenKey.getDigest().equals(digest)) {
                logger.debug("Key verification failed.");
                return null;
            }
        } catch (ServerException e) {
            return null;
        }

        /* Verify extended information. */
        if (!verifyExtendedInformation(tokenKey.getExtendedInformation())) {
            logger.debug("Token information verification failed.");
            return null;
        }

        return new RestToken(key, SecurityConfiguration.TOKEN_TYPE, tokenKey);
    }

    @Override
    public final Token allocateToken(String extendedInformation) {
        long creationTime = System.currentTimeMillis();

        TokenKey tokenKey = new TokenKey();
        tokenKey.setRandom(secureRandomStringGenerator.generateUniqueRandomString(32));
        tokenKey.setScope(getTokenScope());
        tokenKey.setCreationTime(creationTime);
        tokenKey.setExtendedInformation(extendedInformation);

        final String digest;
        try {
            digest = sha(tokenKey);
            tokenKey.setDigest(digest);
        } catch (ServerException e) {
            logger.warn("Failed to allocate token", e.getMessage());
        }

        byte[] payload = getTokenKeySerializer().serialize(tokenKey);
        if (logger.isTraceEnabled()) {
            logger.trace("allocated token: {}", new String(payload));
        }
        String key = Utf8.decode(Base64.encode(payload));
        return new RestToken(key, SecurityConfiguration.TOKEN_TYPE, tokenKey);
    }
}
