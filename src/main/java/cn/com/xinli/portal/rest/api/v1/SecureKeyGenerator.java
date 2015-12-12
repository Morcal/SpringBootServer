package cn.com.xinli.portal.rest.api.v1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class SecureKeyGenerator {
    /** Log. */
    private static final Log log = LogFactory.getLog(SecureKeyGenerator.class);

    /** Secure random generator. */
    private static SecureRandom random;

    /** Generated token values. */
    private final Set<String> generated = Collections.synchronizedSet(new HashSet<>());

    public SecureKeyGenerator() {
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
    public String generateUniqueRandomString() {
        String randomString;
        synchronized (generated) {
            do {
                randomString = new BigInteger(130, random).toString(32);
            } while (!generated.add(randomString));
        }
        return randomString;
    }
}
