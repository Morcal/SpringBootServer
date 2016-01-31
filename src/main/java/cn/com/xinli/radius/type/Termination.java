package cn.com.xinli.radius.type;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum Termination {
    DEFAULT(0, "Default"),
    RADIUS_REQUEST(1, "RADIUS-Request");

    public static final String RADIUS_NAME = "Termination-Action";

    private final int value;
    private final String name;

    Termination(int value, String name) {
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
