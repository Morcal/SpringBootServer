package cn.com.xinli.radius.type;

import org.apache.commons.codec.binary.Hex;

import java.util.Objects;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class ValueTypes {
    public class StringValueType implements ValueType<String> {
        @Override
        public byte[] encode(String value) {
            return value.getBytes();
        }

        @Override
        public String decode(byte[] data) {
            Objects.requireNonNull(data, "decode data is empty.");
            return new String(data);
        }
    }

    public class OctetsValueType implements ValueType<String> {
        @Override
        public byte[] encode(String value) {
            return value.getBytes();
        }

        @Override
        public String decode(byte[] data) {
            return Hex.encodeHexString(data);
        }
    }
}
