package cn.com.xinli.portal.protocol.huawei;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class HuaweiV2 {

    static class Def {
        final int code;
        final String name;

        public Def(int code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    static final Def definitions[] = {
        new Def(0x01, "REQ_CHALLENGE")
    };
    /**
     * Packet type Codes.
     */
    static final int REQ_CHALLENGE = 0x01;
    static final int ACK_CHALLENGE = 0x02;
    static final int REQ_AUTH = 0x03;
    static final int ACK_AUTH = 0x04;
    static final int REQ_LOGOUT = 0x05;
    static final int ACK_LOGOUT = 0x06;
    static final int AFF_ACK_AUTH = 0x07;
    public static final int NTF_LOGOUT = 0x08 ;
    //version V2.0.add
    private static final int REQ_INFO = 0x09;
    private static final int ACK_INFO = 0x0A;
    protected static final int NTF_USERDISCOVERY = 0x0b;
    protected static final int NTF_USERIPCHANGE = 0x0c;
    protected static final int AFF_NTF_USERIPCHANGE = 0x0d;
    //WEB_Status_NOTIFY
    public static final int WEB_STATUS_NOTIFY = 0x81;
    public static final int WEB_ACK_STATUS_NOTIFY = 0x82;
    /**
     * Maximum packet length.
     */
    public static final int MAX_PACKET_LENGTH = 1024;
    private static final int MIN_PACKET_LENGTH = 32;

    /**
     * Auth Type chap/pap cons
     */
    public static final int AUTH_CHAP = 0x00;
    public static final int AUTH_PAP = 0x01;
}
