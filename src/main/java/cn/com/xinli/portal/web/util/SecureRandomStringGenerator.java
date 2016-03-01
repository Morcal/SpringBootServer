package cn.com.xinli.portal.web.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Secure random string generator.
 *
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/12.
 */
@Component
public class SecureRandomStringGenerator {
    /**
     * Log.
     */
    private final Logger logger = LoggerFactory.getLogger(SecureRandomStringGenerator.class);

    /**
     * Secure random generator.
     */
    private static SecureRandom random;

    /**
     * Generated token values.
     */
    private final Set<String> generated = Collections.synchronizedSet(new HashSet<>());

    private static final int MAX_CACHED_STRING_SIZE = 1000;

    public SecureRandomStringGenerator() {
        try {
            logger.debug("Trying to get SHA1PRNG random generator.");
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            logger.warn("Missing secure random of SHA1PRNG", e);
            random = new SecureRandom();
        }
        random.setSeed(random.generateSeed(64));
    }

    /**
     * Generate an unique secure random string.
     * <p>
     * Each ASCII character takes '5' bits.
     * <code>2^5 = 32</code>.
     *
     * @param size string size.
     * @return an unique secure characters random string with given size.
     */
    public String generateUniqueRandomString(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("String length must be positive.");
        }

        String randomString;
        synchronized (generated) {
            /* Sanity check. */
            if (generated.size() > MAX_CACHED_STRING_SIZE) {
                logger.info("Discarding generated random strings.");
                generated.clear();
            }

            do {
                randomString = new BigInteger(5 * size, random).toString(32);
            } while (!generated.add(randomString));
        }
        return randomString;
    }
}
