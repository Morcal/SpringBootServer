package cn.com.xinli.portal.core;

/**
 * Portal authentication type.
 *
 * <p>This class provides supported authentication types fro system.
 *
 * <p>{@link #CHAP} works in the environments that remote server need a challenge
 * process before authentication.
 *
 * <p>{@link #PAP} works in the environments that remote server need
 * client to encrypt user credentials (password) when authenticating.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public enum AuthType {
    /** CHAP authentication type. */
    CHAP(0x00),

    /** PAP authentication type. */
    PAP(0x01);

    private final int code;

    AuthType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    /**
     * Create authentication type by type name.
     * @param type type name.
     * @return authentication type.
     */
    public static AuthType of(String type) {
        for (AuthType t : AuthType.values()) {
            if (t.name().equalsIgnoreCase(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Authentication type: " + type + " not exists.");
    }

    /**
     * Create authentication type by type code.
     * @param code type code.
     * @return authentication type.
     */
    public static AuthType valueOf(int code) {
        for (AuthType t : AuthType.values()) {
            if (t.code() == code) {
                return t;
            }
        }
        throw new IllegalArgumentException("Authentication type code: " + code + " not exists.");
    }
}
