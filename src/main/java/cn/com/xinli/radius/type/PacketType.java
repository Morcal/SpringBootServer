package cn.com.xinli.radius.type;

/**
 * Radius Packet Type.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public enum PacketType {
    ACCESS_REQUEST(1, "Access-Request"),
    ACCESS_ACCEPT(2, "Access-Accept"),
    ACCESS_REJECT(3, "Access-Reject"),
    ACCOUNTING_REQUEST(4, "Accounting-Request"),
    ACCOUNTING_RESPONSE(5, "Accounting-Response"),
    ACCOUNTING_STATUS(6, "Accounting-Status"),
    PASSWORD_REQUEST(7, "Password-Request"),
    PASSWORD_ACCEPT(8, "Password-Accept"),
    PASSWORD_REJECT(9, "Password-Reject"),
    ACCOUNTING_MESSAGE(10, "Accounting-Message"),
    ACCESS_CHALLENGE(11, "Access-Challenge"),
    STATUS_SERVER(12, "Status-Server"),
    STATUS_CLIENT(13, "Status-Client"),
    DISCONNECT_REQUEST(40, "Disconnect-Request"),
    DISCONNECT_ACK(41, "Disconnect-ACK"),
    DISCONNECT_NAK(42, "Disconnect-NAK"),
    COA_REQUEST(43, "CoA-Request"),
    COA_ACK(44, "CoA-ACK"),
    COA_NAK(45, "CoA-NAK"),
    STATUS_REQUEST(46, "Status-Request"),
    STATUS_ACCEPT(47, "Status-Accept"),
    STATUS_REJECT(48, "Status-Reject"),
    RESERVED(255, "Reserved");

    private final int value;
    private final String name;

    PacketType(int value, String name) {
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
