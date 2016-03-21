package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.TransportUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Packet supports HUAWEI Portal protocol.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 */
public final class Packet {
    /** HUAWEI portal version. */
    private int version;

    /** Operation type. */
    private int type;

    /** Authentication type. */
    private int authType;

    /** Reserved (not used). */
    private int reserved;

    /** Random number generated by portal client. unique in-a-time-range. */
    private int serialNum;

    /**
     * Request id generated by portal server (NAS/BRAS).
     *
     * <p>Subsequent requests to NAS/BRAS may require associated "reqId"
     * which be assigned by NAS/BRAS in previous request(s).
     *
     * <p>In a sendTimeout packet (which type is {@link RequestType#REQ_LOGOUT}),
     * <ul>
     *     <li><code>reqId = 0</code> when challenge timeout.</li>
     *     <li><code>reqId = auth_request.reqId</code> when authentication request timeout.</li>
     * </ul>
     */
    private int reqId;

    /**
     * Packet ip address(user address) for ipv4 (4 bytes),
     * ipv6 should be carried within packet's attributes.
     */
    private byte[] ip; // 4 bytes.

    /** User port (not used). */
    private int port;

    /** Error code, work with {@link #type}. */
    private int error;

    /** AttributeType size. */
    private int attrs;

    /** Authenticator for V2.0 and above. */
    private byte[] authenticator;

    /** Attributes. */
    private Collection<Attribute> attributes = new ArrayList<>();

    /** Maximum packet length. */
    public static final int MAX_LENGTH = 1024;

    /**
     * Add an attribute to a {@link Packet}.
     * <p>Caller should manipulate {@link #attrs} manually.</p>
     * @param type attribute type.
     * @param value value in bytes.
     */
    public void addAttribute(AttributeType type, byte[] value) {
        attributes.add(new Attribute(type.code(), value));
    }

    /**
     * Get attribute value.
     * @param type attribute type.
     * @return value in bytes.
     */
    public byte[] getAttribute(AttributeType type) {
        for (Attribute attribute : attributes) {
            if (attribute.getType() == type.code())
                return attribute.getValue();
        }
        return new byte[0];
    }

    /** HUAWEI packet attribute (TLV). */
    public static class Attribute {
        private final int type;
        private final int length;
        private final byte[] value;

        public Attribute(int type, byte[] value) {
            this.type = type;
            this.value = value;
            this.length = value.length + 2;
        }

        public int getLength() {
            return length;
        }

        public int getType() {
            return type;
        }

        public byte[] getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "AttributeType{" +
                    "length=" + length +
                    ", type=" + type +
                    ", value=" + TransportUtils.bytesToHexString(value) +
                    '}';
        }
    }

    public int getAttrs() {
        return attrs;
    }

    public void setAttrs(int attrs) {
        this.attrs = attrs;
    }

    public int getError() {
        return error;
    }

    public byte[] getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(byte[] authenticator) {
        this.authenticator = authenticator;
    }

    public void setError(int error) {
        this.error = error;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public byte[] getIp() {
        return ip;
    }

    public void setIp(byte[] ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "version=" + version +
                ", type=" + type +
                ", authType=" + authType +
                ", reserved=" + reserved +
                ", serialNum=" + serialNum +
                ", reqId=" + reqId +
                ", ip=" + TransportUtils.bytesToHexString(ip) +
                ", port=" + port +
                ", error=" + error +
                ", attrs=" + attrs +
                ", authenticator=" + TransportUtils.bytesToHexString(authenticator) +
                ", attributes=" + attributes +
                '}';
    }
}