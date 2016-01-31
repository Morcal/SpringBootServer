package cn.com.xinli.portal.transport.huawei;

/**
 * HUAWEI protocol version.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum Version {
    /** HUAWEI protocol v1. */
    V1(0x01),

    /** HUAWEI protocol v2. */
    V2(0x02);

    /** Version value. */
    private final int value;

    Version(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
