package cn.com.xinli.radius.type;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum AuthenticationType {
    RADIUS(1, "RADIUS"),
    Local(2, "Local"),
    Remote(3, "Remote"),
    Diameter(4, "Diameter");

    public static final String RADIUS_NAME = "Acct-Authentic";

    private final int value;
    private final String name;

    AuthenticationType(int value, String name) {
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
