package cn.com.xinli.radius.type;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public enum AcctTerminateCause {
    USER_REQUEST(1, "User-Request"),
    LOST_CARRIER(2, "Lost-Carrier"),
    LOST_SERVICE(3, "Lost-Service"),
    IDLE_TIMEOUT(4, "Idle-Timeout"),
    SESSION_TIMEOUT(5, "Session-Timeout"),
    ADMIN_RESET(6, "Admin-Reset"),
    ADMIN_REBOOT(7, "Admin-Reboot"),
    PORT_ERROR(8, "Port-Error"),
    NAS_ERROR(9, "NAS-Error"),
    NAS_REQUEST(10, "NAS-Request"),
    NAS_REBOOT(11, "NAS-Reboot"),
    PORT_UNNEEDED(12, "Port-Unneeded"),
    PORT_PREEMPTED(13, "Port-Preempted"),
    PORT_SUSPENDED(14, "Port-Suspended"),
    SERVICE_UNAVAILABLE(15, "Service-Unavailable"),
    CALLBACK(16, "Callback"),
    USER_ERROR(17, "User-Error"),
    HOST_REQUEST(18, "Host-Request"),
    SUPPLICANT_RESTART(19, "Supplicant-Restart"),
    REAUTHENTICATION_FAILURE(20, "Reauthentication-Failure"),
    PORT_REINIT(21, "Port-Reinit"),
    PORT_DISABLED(22, "Port-Disabled");

    public static final String RADIUS_NAME = "Acct-Terminate-Cause";

    private final int value;
    private final String name;

    AcctTerminateCause(int value, String name) {
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
