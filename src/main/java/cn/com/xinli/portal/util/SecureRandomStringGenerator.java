package cn.com.xinli.portal.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Secure random string generator.
 *
 * Project: portal
 *
 * @author zhoupeng 2015/12/12.
 */
public class SecureRandomStringGenerator implements RandomStringGenerator {
    /**
     * Log.
     */
    private static final Log log = LogFactory.getLog(SecureRandomStringGenerator.class);

    /**
     * Secure random generator.
     */
    private static SecureRandom random;

    /**
     * Generated token values.
     */
    private final Set<String> generated = Collections.synchronizedSet(new HashSet<>());

    public SecureRandomStringGenerator() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (Exception e) {
            log.warn(e);
            random = new SecureRandom();
        }
        random.setSeed(random.generateSeed(64));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Each ASCII character takes '5' bits.
     * <code>2^5 = 32</code>.
     *
     * @param size string size.
     * @return an unique secure characters random string with given size.
     */
    @Override
    public String generateUniqueRandomString(int size) {
        String randomString;
        synchronized (generated) {
            do {
                randomString = new BigInteger(5 * size, random).toString(32);
            } while (!generated.add(randomString));
        }
        return randomString;
    }
}
