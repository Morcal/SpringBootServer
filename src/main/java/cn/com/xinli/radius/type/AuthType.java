package cn.com.xinli.radius.type;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public enum AuthType {
    CHAP(0, "chap"),
    PAP(1, "pap");

    private final int value;
    private final String name;

    AuthType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
