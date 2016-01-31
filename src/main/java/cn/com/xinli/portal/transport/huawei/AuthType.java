package cn.com.xinli.portal.transport.huawei;

/**
 * HUAWEI portal authentication type.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/30.
 */
public enum AuthType {
    /** CHAP authentication type. */
    CHAP(0x00, "chap"),
    /** PAP authentication type. */
    PAP(0x01, "pap");

    private final int code;
    private final String name;

    AuthType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int code() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static AuthType valueOf(int code) {
        for (AuthType t : AuthType.values()) {
            if (t.code() == code) {
                return t;
            }
        }
        throw new IllegalArgumentException("Authentication type code: " + code + " not exists.");
    }
}
