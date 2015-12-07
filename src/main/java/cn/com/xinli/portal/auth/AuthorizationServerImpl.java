package cn.com.xinli.portal.auth;

import cn.com.xinli.portal.Session;
import cn.com.xinli.portal.persist.SessionRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/7.
 */
public class AuthorizationServerImpl implements AuthorizationServer {
    /** Log. */
    private static final Log log = LogFactory.getLog(AuthorizationServerImpl.class);

    /** Default session token expire time (in seconds). */
    public static final long DEFAULT_SESSION_TOKEN_EXPIRE = 3600;

    /** Default access token expire time (in seconds). */
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRE = 3600;

    /** Secure random generator seed length. */
    private static final int RANDOM_GENERATOR_SEED_LENGTH = 32;

    /** Secure random generator. */
    private SecureRandom random;

    /** Generated token values. */
    private final Set<String> generated = Collections.synchronizedSet(new HashSet<>());

    /** In memory session token storage. */
    private final Map<String, SessionToken> sessionTokens = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    private SessionRepository sessionRepository;

    public AuthorizationServerImpl() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            log.warn(e);
            random = new SecureRandom();
        }
        random.setSeed(random.generateSeed(64));
    }

    /**
     * Generate an unique secure random string.
     * @return unique secure random string.
     */
    private String generateRandomString() {
        String randomString;
        synchronized (generated) {
            do {
                randomString = new BigInteger(130, random).toString(32);
            } while (!generated.add(randomString));
        }
        return randomString;
    }

    @Override
    public SessionToken generateSessionToken(Session session) {
        String value = generateRandomString();
        SessionToken token = new SessionToken(session.getId(), value, DEFAULT_SESSION_TOKEN_EXPIRE);
        log.debug("token: " + token + " created.");
        return token;
    }

    @Override
    public AccessToken generateAccessToken(String clientId, String secret) {
        return null;
    }

    @Override
    public boolean validateSessionToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("session token can not be empty.");
        }

        SessionToken sessionToken;
        synchronized (sessionTokens) {
            if (!sessionTokens.containsKey(token))
                return false;

            sessionToken = sessionTokens.get(token);
        }

        if (!sessionToken.validate()) {
            return false;
        }

        try {
            Session session = sessionRepository.findOne(sessionToken.getSessionId());
            log.debug("session with token: " + token + " found, -> " + session);
        } catch (EntityNotFoundException e) {
            log.debug("session with token: " + token + " already gone.");
            return false;
        }

        return true;
    }

    @Override
    public boolean validateAccessToken(String token) {
        return false;
    }
}
