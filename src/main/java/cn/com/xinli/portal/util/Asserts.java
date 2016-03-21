package cn.com.xinli.portal.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Assert utility.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/3/14.
 */
public class Asserts {
    /**
     * Check that the specified string is not null or does not contains any
     * characters. <code>value != null && value.length > 0</code>.
     * @param value string
     * @return give string if not empty.
     * @throws IllegalArgumentException if {@code value} is blank.
     */
    public static String notBlank(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException();
        }
        return value;
    }

    /**
     * Check that the specified string is not null or does not contains any
     * characters. <code>value != null && value.length > 0</code>, throws a
     * customized {@link NullPointerException} if it is.
     * @param value string
     * @return {@code string} if {@code string} is not blank.
     */
    public static String notBlank(String value, String name) {
        notBlank(name);
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException(name + " can not be blank");
        }
        return value;
    }

    /**
     * Check that the specified class is the same or subclass of expected class.
     * @param expect expected class.
     * @param actual actual class.
     * @param <T> expected type.
     * @return expected class if {@code actual} is same or subclass of {@code expect}.
     * @throws RuntimeException if {@code actual} is not same of subclass of
     * {@code expect}.
     */
    public static <T> Class<T> isType(Class<T> expect, Class<?> actual) {
        Objects.requireNonNull(expect);
        Objects.requireNonNull(actual);
        if (!expect.isAssignableFrom(actual)) {
            throw new RuntimeException(
                    actual.getName() + " is not same class or subclass of " + expect.getName());
        }
        return expect;
    }

    /**
     * Return object's class as given class.
     * @param object object.
     * @param type type class
     * @param <T> type.
     * @return class of type.
     * @throws RuntimeException if {@code object} is null or object's is not
     * an instance of class T.
     */
    public static <T> Class<T> asType(Object object, Class<T> type) {
        Objects.requireNonNull(type, "check type");

        if (object == null || !(type.isInstance(object))) {
            throw new RuntimeException("object is not type of " + type.getName());
        }

        return type;
    }
}
