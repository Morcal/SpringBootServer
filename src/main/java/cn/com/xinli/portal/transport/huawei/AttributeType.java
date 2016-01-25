package cn.com.xinli.portal.transport.huawei;

/**
 * Huawei portal protocol attribute.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/24.
 */
public enum AttributeType {
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

    AttributeType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
