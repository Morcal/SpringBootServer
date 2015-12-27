package cn.com.xinli.portal.rest.token;

import cn.com.xinli.portal.ServerConfig;
import cn.com.xinli.portal.util.SecureRandomStringGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;

/**
 * Abstract Token Service
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/28.
 */
@Service
public abstract class AbstractTokenService implements TokenService, InitializingBean {
    /** Log. */
    private static final Log log = LogFactory.getLog(AbstractTokenService.class);

    @Autowired
    private SecureRandomStringGenerator secureRandomStringGenerator;

    @Autowired
    private ServerConfig serverConfig;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(serverConfig);
        Assert.notNull(secureRandomStringGenerator);
    }

    /**
     * Get token scope.
     * @return token scope.
     */
    protected abstract TokenScope getTokenScope();

    /**
     * Verify extended information in token.
     * @param extendedInformation extended information in token.
     * @return true if extended information in token is valid.
     */
    protected abstract boolean verifyExtendedInformation(String extendedInformation);

    protected abstract int getTtl();

    /**
     * Create token key content.
     * @param scope token scope.
     * @param creationTime token creation time.
     * @param extendedInformation extended information.
     * @param random server random string.
     * @return content string.
     */
    private String createContent(TokenScope scope, long creationTime, String extendedInformation, String random) {
        StringJoiner joiner = new StringJoiner(":");
        joiner.add(scope.name())
                .add(String.valueOf(creationTime))
                .add(random)
                .add(extendedInformation);

        return joiner.toString();
    }

    /**
     * Create SHA summary.
     * @param content content to create summary.
     * @return SHA summary.
     */
    private String sha(String content) {
        return Sha512DigestUtils.shaHex(content + ":" + serverConfig.getPrivateKey());
    }

    @Override
    public final Token verifyToken(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        long now = System.currentTimeMillis();

        String[] tokens = StringUtils.delimitedListToStringArray(Utf8.decode(Base64.decode(Utf8.encode(key))), ":");

        if (tokens == null || tokens.length != 5) {
            return null;
        }

        TokenScope scope;
        try {
            scope = TokenScope.valueOf(tokens[0]);
            if (scope != getTokenScope()) {
                log.debug("> Invalid token scope.");
            }
        } catch (IllegalArgumentException e) {
            log.debug("> Invalid token scope.");
            return null;
        }

        long creationTime;
        try {
            creationTime = Long.decode(tokens[1]);
        } catch (NumberFormatException e) {
            log.debug("> Invalid token creation time.");
            return null;
        }

        String random = tokens[2];
        String extendedInformation = tokens[3];
        String sha512Hex = tokens[4];

        if (StringUtils.isEmpty(extendedInformation)) {
            log.debug("> Empty token extended information.");
            return null;
        }

        if ((now - creationTime) / 1000L > getTtl()) {
            log.debug("> Token expired.");
            return null;
        }

        String expectedSha512Hex = sha(createContent(scope, creationTime, extendedInformation, random));

        if (!sha512Hex.equals(expectedSha512Hex)) {
            log.debug("> Key verification failed.");
            return null;
        }

        if (!verifyExtendedInformation(extendedInformation)) {
            log.debug("> Token information verification failed.");
            return null;
        }

        return new RestToken(key, creationTime, getTokenScope(), extendedInformation);
    }


    @Override
    public final Token allocateToken(String extendedInformation) {
        long creationTime = System.currentTimeMillis();
        String random = secureRandomStringGenerator.generateUniqueRandomString(32);
        String content = createContent(getTokenScope(), creationTime, extendedInformation, random);
        String sha512Hex = sha(content);
        String keyPayload = content + ":" + sha512Hex;
        String key = Utf8.decode(Base64.encode(Utf8.encode(keyPayload)));
        return new RestToken(key, creationTime, getTokenScope(), extendedInformation);
    }
}
