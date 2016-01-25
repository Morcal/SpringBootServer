package cn.com.xinli.portal.transport.huawei;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Portal Operation types.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum RequestType {
    REQ_CHALLENGE(0x01),
    ACK_CHALLENGE(0x02),
    REQ_AUTH(0x03),
    ACK_AUTH(0x04),
    REQ_LOGOUT(0x05),
    ACK_LOGOUT(0x06),
    AFF_ACK_AUTH(0x07),
    NTF_LOGOUT(0x08),
    REQ_INFO(0x09),
    ACK_INFO(0x0a),
    NTF_USERDISCOVERY(0x0b),
    NTF_USERIPCHANGE(0x0c),
    AFF_NTF_USERIPCHANGE(0x0d),
    ACK_NTF_LOGOUT(0x0e),
    //WEB_Status_NOTIFY
    WEB_STATUS_NOTIFY(0x81),
    WEB_ACK_STATUS_NOTIFY(0x82);

    private final int code;

    RequestType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static Optional<RequestType> valueOf(int code) {
        return Stream.of(values()).filter(v -> v.code() == code).findFirst();
    }
}
