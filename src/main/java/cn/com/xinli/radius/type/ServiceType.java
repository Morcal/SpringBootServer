package cn.com.xinli.radius.type;

/**
 * Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum ServiceType {
    LoginUser(1, "Login-User"),
    FramedUser(2, "Framed-User"),
    CallbackLoginUser(3, "Callback-Login-User"),
    CallbackFramedUser(4, "Callback-Framed-User"),
    OutboundUser(5, "Outbound-User"),
    AdministrativeUser(6, "Administrative-User"),
    NASPromptUser(7, "NAS-Prompt-User"),
    AuthenticateOnly(8, "Authenticate-Only"),
    CallbackNASPrompt(9, "Callback-NAS-Prompt"),
    CallCheck(0, "Call-Check"),
    CallbackAdministrative(1, "Callback-Administrative"),
    Voice(2, "Voice"),
    Fax(3, "Fax"),
    ModemRelay(4, "Modem-Relay"),
    IAPPRegister(5, "IAPP-Register"),
    IAPPAPCheck(6, "IAPP-AP-Check");

    public static final String RADIUS_NAME = "Servcie-Type";

    private final int value;

    private final String name;

    ServiceType(int value, String name) {
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
