package cn.com.xinli.portal.core;

/**
 * Serializer.
 *
 * <p>Project: xpws
 *
 * @param <T> target type.
 *
 * @author zhoupeng 2016/1/30.
 */
public interface Serializer<T> {
    /**
     * Serialize java object to bytes.
     * @param t java object.
     * @return bytes.
     */
    byte[] serialize(T t);

    /**
     * Translate bytes into java object.
     * @param bytes input bytes.
     * @return java object.
     */
    T deserialize(byte[] bytes);
}
