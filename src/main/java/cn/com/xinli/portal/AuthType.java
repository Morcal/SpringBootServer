package cn.com.xinli.portal;

/**
 * Portal authentication type.
 *
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

    public static AuthType valueOf(int code) {
        for (AuthType t : AuthType.values()) {
            if (t.code() == code) {
                return t;
            }
        }
        throw new RuntimeException("Authentication code: " + code + " not supported.");
    }
}
