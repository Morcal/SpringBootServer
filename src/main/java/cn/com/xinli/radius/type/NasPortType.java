package cn.com.xinli.radius.type;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum NasPortType {
    Async(0, "Async"),
    Sync(1, "Sync"),
    ISDN(2, "ISDN"),
    ISDN_V120(3, "ISDN-V120"),
    ISDN_V110(4, "ISDN_V110"),
    Virtual(5, "Virtual"),
    PIAFS(6, "PIAFS"),
    HDLC_Clear_Channel(7, "HDLC-Clear-Channel"),
    X25(8, "X.25"),
    X75(9, "X.75"),
    G3_FAX(10, "G.3-Fax"),
    SDSL(11, "SDSL"),
    ADSL_CAP(12, "ADSL-CAP"),
    ADSL_DMT(13, "ADSL-DMT"),
    IDSL(14, "IDSL"),
    Ethernet(15, "Ethernet"),
    xDSL(16, "xDSL"),
    Cable(17, "Cable"),
    Wireless_Other(18, "Wireless-Other"),
    Wireless_802_11(19, "Wireless-802.11"),
    Token_Ring(20, "Token-Ring"),
    FDDI(21, "FDDI"),
    Wireless_CDMA2000(22, "Wireless-CDMA2000"),
    Wireless_UMTS(23, "Wireless-UMTS"),
    Wireless_1X_EV(24, "Wireless-1X-EV"),
    IAPP(25, "IAPP");

    public static final String RADIUS_NAME = "NAS-Port-Type";

    private final int value;
    private final String name;

    NasPortType(int value, String name) {
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
