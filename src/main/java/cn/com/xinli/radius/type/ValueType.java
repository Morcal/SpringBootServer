package cn.com.xinli.radius.type;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public interface ValueType<T> {
    byte[] encode(T value);
    T decode(byte[] data);
}
