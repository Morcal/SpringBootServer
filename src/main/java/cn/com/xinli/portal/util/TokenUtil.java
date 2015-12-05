package cn.com.xinli.portal.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/5.
 */
public class TokenUtil {
    /** Token length. */
    private static final int TOKEN_LENGTH = 32;

    /** Secure random generator. */
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate 130 bits from a cryptographically secure random
     * bit generator, and encoding them in base-32. 128 bits
     * is considered to be cryptographically strong,
     * but each digit in a base 32 number can encode 5 bits,
     * so 128 is rounded up to the next multiple of 5.
     * This encoding is compact and efficient, with 5 random
     * bits per character. Compare this to a random UUID,
     * which only has 3.4 bits per character in standard layout,
     * and only 122 random bits in total.
     *
     * @return secure random string with length {@link #TOKEN_LENGTH}.
     */
    public static String generate() {
        return new BigInteger(130, random).toString(TOKEN_LENGTH);
    }
}
