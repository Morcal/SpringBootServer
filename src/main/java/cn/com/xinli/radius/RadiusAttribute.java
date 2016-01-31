package cn.com.xinli.radius;

import cn.com.xinli.radius.type.Attribute;

/**
 * RADIUS attribute.
 *
 * <p>RADIUS attributes are length-type-value pairs.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusAttribute<T> {
    Attribute attribute;
    byte[] value;

    T getValue(Class<T> cls) {
        return null;
    }
}
