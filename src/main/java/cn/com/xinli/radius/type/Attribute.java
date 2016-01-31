package cn.com.xinli.radius.type;

/**
 * RADIUS attribute.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/26.
 */
public enum Attribute {
    USER_NAME(1, "User-Name", "string"),
    USER_PASSWORD(2, "User-Password", "octets"),
    CHAP_PASSWORD(3, "CHAP-Password", "octets"),
    NAS_IP_ADDRESS(4, "NAS-IP-Address", "ipaddr"),
    NAS_PORT(5, "NAS-Port", "integer"),
    SERVICE_TYPE(6, "Service-Type", "integer"),
    FRAMED_PROTOCOL(7, "Framed-Protocol", "integer"),
    FRAMED_IP_ADDRESS(8, "Framed-IP-Address", "ipaddr"),
    FRAMED_IP_NETMASK(9, "Framed-IP-Netmask", "ipaddr"),
    FRAMED_ROUTING(10, "Framed-Routing", "integer"),
    FILTER_ID(11, "Filter-Id", "string"),
    FRAMED_MTU(12, "Framed-MTU", "integer"),
    FRAMED_COMPRESSION(13, "Framed-Compression", "integer"),
    LOGIN_IP_HOST(14, "Login-IP-Host", "ipaddr"),
    LOGIN_SERVICE(15, "Login-Service", "integer"),
    LOGIN_TCP_PORT(16, "Login-TCP-Port", "integer"),
    REPLY_MESSAGE(18, "Reply-Message", "string"),
    CALLBACK_NUMBER(19, "Callback-Number", "string"),
    CALLBACK_ID(20, "Callback-Id", "string"),
    FRAMED_ROUTE(22, "Framed-Route", "string"),
    FRAMED_IPX_NETWORK(23, "Framed-IPX-Network", "ipaddr"),
    STATE(24, "State", "octets"),
    CLASS(25, "Class", "octets"),
    VENDOR_SPECIFIC(26, "Vendor-Specific", "octets"),
    SESSION_TIMEOUT(27, "Session-Timeout", "integer"),
    IDLE_TIMEOUT(28, "Idle-Timeout", "integer"),
    TERMINATION_ACTION(29, "Termination-Action", "integer"),
    CALLED_STATION_ID(30, "Called-Station-Id", "string"),
    CALLING_STATION_ID(31, "Calling-Station-Id", "string"),
    NAS_IDENTIFIER(32, "NAS-Identifier", "string"),
    PROXY_STATE(33, "Proxy-State", "octets"),
    LOGIN_LAT_SERVICE(34, "Login-LAT-Service", "string"),
    LOGIN_LAT_NODE(35, "Login-LAT-Node", "string"),
    LOGIN_LAT_GROUP(36, "Login-LAT-Group", "octets"),
    FRAMED_APPLETALK_LINK(37, "Framed-AppleTalk-Link", "integer"),
    FRAMED_APPLETALK_NETWORK(38, "Framed-AppleTalk-Network", "integer"),
    FRAMED_APPLETALK_ZONE(39, "Framed-AppleTalk-Zone", "string"),
    ACCT_STATUS_TYPE(40, "Acct-Status-Type", "integer"),
    ACCT_DELAY_TIME(41, "Acct-Delay-Time", "integer"),
    ACCT_INPUT_OCTETS(42, "Acct-Input-Octets", "integer"),
    ACCT_OUTPUT_OCTETS(43, "Acct-Output-Octets", "integer"),
    ACCT_SESSION_ID(44, "Acct-Session-Id", "string"),
    ACCT_AUTHENTIC(45, "Acct-Authentic", "integer"),
    ACCT_SESSION_TIME(46, "Acct-Session-Time", "integer"),
    ACCT_INPUT_PACKETS(47, "Acct-Input-Packets", "integer"),
    ACCT_OUTPUT_PACKETS(48, "Acct-Output-Packets", "integer"),
    ACCT_TERMINATE_CAUSE(49, "Acct-Terminate-Cause", "integer"),
    ACCT_MULTI_SESSION_ID(50, "Acct-Multi-Session-Id", "string"),
    ACCT_LINK_COUNT(51, "Acct-Link-Count", "integer"),
    ACCT_INPUT_GIGAWORDS(52, "Acct-Input-Gigawords", "integer"),
    ACCT_OUTPUT_GIGAWORDS(53, "Acct-Output-Gigawords", "integer"),
    EVENT_TIMESTAMP(55, "Event-Timestamp", "date"),
    CHAP_CHALLENGE(60, "CHAP-Challenge", "octets"),
    NAS_PORT_TYPE(61, "NAS-Port-Type", "integer"),
    PORT_LIMIT(62, "Port-Limit", "integer"),
    LOGIN_LAT_PORT(63, "Login-LAT-Port", "integer"),
    ACCT_TUNNEL_CONNECTION(68, "Acct-Tunnel-Connection", "string"),
    ARAP_PASSWORD(70, "ARAP-Password", "string"),
    ARAP_FEATURES(71, "ARAP-Features", "string"),
    ARAP_ZONE_ACCESS(72, "ARAP-Zone-Access", "integer"),
    ARAP_SECURITY(73, "ARAP-Security", "integer"),
    ARAP_SECURITY_DATA(74, "ARAP-Security-Data", "string"),
    PASSWORD_RETRY(75, "Password-Retry", "integer"),
    PROMPT(76, "Prompt", "integer"),
    CONNECT_INFO(77, "Connect-Info", "string"),
    CONFIGURATION_TOKEN(78, "Configuration-Token", "string"),
    EAP_MESSAGE(79, "EAP-Message", "octets"),
    MESSAGE_AUTHENTICATOR(80, "Message-Authenticator", "octets"),
    ARAP_CHALLENGE_RESPONSE(84, "ARAP-Challenge-Response", "string"),
    ACCT_INTERIM_INTERVAL(85, "Acct-Interim-Interval", "integer"),
    NAS_PORT_ID(87, "NAS-Port-Id", "string"),
    FRAMED_POOL(88, "Framed-Pool", "string"),
    NAS_IPV6_ADDRESS(95, "NAS-IPv6-Address", "octets"),
    FRAMED_INTERFACE_ID(96, "Framed-Interface-Id", "octets"),
    FRAMED_IPV6_PREFIX(97, "Framed-IPv6-Prefix", "octets"),
    LOGIN_IPV6_HOST(98, "Login-IPv6-Host", "octets"),
    FRAMED_IPV6_ROUTE(99, "Framed-IPv6-Route", "string"),
    FRAMED_IPV6_POOL(100, "Framed-IPv6-Pool", "string"),
    DIGEST_RESPONSE(206, "Digest-Response", "string"),
    DIGEST_ATTRIBUTES(207, "Digest-Attributes", "octets");

    private final int value;

    private final String name;

    private final String type;

    Attribute(int value, String name, String type) {
        this.value = value;
        this.name = name;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
