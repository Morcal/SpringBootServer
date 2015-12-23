package cn.com.xinli.portal.protocol.huawei;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class Enums {
    public enum Version {
        v1(0x01),
        v2(0x02);

        private final int value;

        Version(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    public enum Type {
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
        //WEB_Status_NOTIFY
        WEB_STATUS_NOTIFY(0x81),
        WEB_ACK_STATUS_NOTIFY(0x82);

        private final int code;

        Type(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        public static Optional<Type> valueOf(int code) {
            return Stream.of(values()).filter(v -> v.code() == code).findFirst();
        }
    }


    public enum AuthError {
        OK(0x00, "认证成功"),
        REJECTED(0x01, "认证请求被拒绝"),
        ALREADY_ONLINE(0x02, "此链接已经建立"),
        WAIT(0x03, "正在认证请稍候"),
        FAILED(0x04, "认证失败");

        private final int code;
        private final String description;

        AuthError(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int code() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Optional<AuthError> valueOf(int code) {
            return Stream.of(values()).filter(v -> v.code() == code).findFirst();
        }
    }

    public enum ChallengeError {
        OK(0x00, "Challenge成功"),
        REJECTED(0x01, "Challenge被拒绝"),
        ALREADY_ONLINE(0x02, "此链接已经建立"),
        WAIT(0x03, "正在认证请稍候"),
        FAILED(0x04, "Challenge失败");

        private final int code;
        private final String description;

        ChallengeError(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int code() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Optional<ChallengeError> valueOf(int code) {
            return Stream.of(values()).filter(v -> v.code() == code).findFirst();
        }
    }

    public enum LogoutRequestError {
        PORTAL_SERVER_REQUEST(0x00),
        NAS_RESPONSE_TIMEOUT(0x01);

        private final int code;

        LogoutRequestError(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }


    public enum LogoutError {
        OK(0x01, "下线成功"),
        REJECTED(0x02, "下线被拒绝"),
        FAILED(0x03, "下线失败");

        private final int code;
        private final String description;

        LogoutError(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int code() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Optional<LogoutError> valueOf(int code) {
            return Stream.of(values()).filter(v -> v.code() == code).findFirst();
        }
    }

    public enum Attribute {
        USER_NAME(0x01),
        PASSWORD(0x02),
        CHALLENGE(0x03),
        CHALLENGE_PASSWORD(0x04),
        TEXT_INFO(0x05),
        /** length: 2 or 10 (in ACK_INFO). unsigned 8 bytes (up-link kilo bytes) */
        UPLINK_FLUX(0x06),
        /** length: 2 or 10 (in ACK_INFO). unsigned 8 bytes (down-link kilo bytes). */
        DOWNLINK_FLUX(0x07),
        /**
         * length: 2 or 2 < x < 37, format: <code>host-slot(2bytes)subslot(1byte)port(2bytes)
         * [VPI(4bytes)VCI(5bytes)] or [OutterVlan(4bytes)InnerVlan(4bytes)]</code>
         */
        PORT(0x08),
        IP_CONFIG(0x09),
        BAS_IP(0x0a),
        USER_MAC(0x0b),
        /**
         * Delay time, length: 6.
         * used for REQ_LOGOUT/NTF_LOGOUT, value = send time - occurred time. */
        DELAY_TIME(0x0c),
        /** User private ip, used for NTF_USERIPCHANGE/NTF_LOGOUT, ip length: 4. total length: 6. */
        USER_PRIVATE_IP(0x0d),
        /** CHAP authentication id, used for Portal Version 9.0, len: 1, total length: 3. */
        CHAP_ID(0xf0),
        /** User ipv6 address, length: 16, total length: 18. */
        USER_IPV6(0xf1);

        private final int code;

        Attribute(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }
}
