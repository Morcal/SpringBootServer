package cn.com.xinli.portal.protocol;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public enum AuthType {
    CHAP(0x00),
    PAP(0x01);

    private final int code;

    AuthType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static AuthType of(String type) {
        for (AuthType t : AuthType.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new RuntimeException("Authentication type: " + type + " not supported.");
    }
}
