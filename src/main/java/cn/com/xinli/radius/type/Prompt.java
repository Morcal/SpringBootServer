package cn.com.xinli.radius.type;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public enum Prompt {
    NO_ECHO(0, "No-Echo"),
    ECHO(1, "Echo");

    public static final String RADIUS_NAME = "Prompt";

    private final int value;
    private final String name;

    Prompt(int value, String name) {
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
