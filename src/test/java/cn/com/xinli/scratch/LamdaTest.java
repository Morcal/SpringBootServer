package cn.com.xinli.scratch;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/11.
 */
public class LamdaTest {

    private Optional<String> guess(int guess) {
        if (guess < 0) {
            return Optional.empty();
        }
        return Optional.of(String.valueOf(guess));
    }

    @Test
    public void testThrowException() {
        Optional<String> g = guess(1);

        try {
            g.ifPresent(s -> {
                if (s.length() < 3)
                    throw new RuntimeException("guess is less than 3.");
            });
        } catch (RuntimeException e) {
            Assert.assertTrue(true);
        }
    }
}
