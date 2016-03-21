package cn.com.xinli.radius.type;

/**
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum LoginServiceType {
    TELNET(0, "Telnet"),
    RLOGIN(0, "Rlogin"),
    TCP_CLEAR(0, "TCP-Clear"),
    PORTMASTER(0, "PortMaster"),
    LAT(0, "LAT"),
    X25_PAD(0, "X25-PAD"),
    X25_T3PO(0, "X25-T3PO"),
    TCP_CLEAR_QUIET(0, "TCP-Clear-Quiet");

    public static final String RADIUS_NAME = "Login-Service";

    private final int value;
    private final String name;

    LoginServiceType(int value, String name) {
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
