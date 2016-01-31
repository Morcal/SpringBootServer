package cn.com.xinli.radius.type;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum AcctStatusType {
    Start(1, "Start"),
    Stop(2, "Stop"),
    Interim_Update(3, "Interim-Update"),
    Alive(3, "Alive"),
    Accounting_On(7, "Accounting-On"),
    Accounting_Off(8, "Accounting-Off"),
    Tunnel_Start(9, "Tunnel-Start"),
    Tunnel_Stop(10, "Tunnel-Stop"),
    Tunnel_Reject(11, "Tunnel-Reject"),
    Tunnel_Link_Start(12, "Tunnel-Link-Start"),
    Tunnel_Link_Stop(13, "Tunnel-Link-Stop"),
    Tunnel_Link_Reject(14, "Tunnel-Link-Reject"),
    Failed(5, "Failed");

    public static final String RADIUS_NAME = "Acct-Status-Type";

    private final int value;
    private final String name;

    AcctStatusType(int value, String name) {
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
