package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.protocol.huawei.Enums;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class Packet {
    /** Huawei portal version. */
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
     * <p>In a nak packet {@link Enums.Type#REQ_LOGOUT},
     * <ul>
     *     <li><code>reqId = 0</code> when challenge timeout.</li>
     *     <li><code>reqId = auth_request.reqId</code> when authentication request timeout.</li>
     * </ul>
     */
    private int reqId;

    private byte[] ip; // 4 bytes.

    /** User port (not used). */
    private int port;

    /** Error code, work with. */
    private int error;

    private int attrs;

    private byte[] authenticator;

    private Collection<Attribute> attributes = new ArrayList<>();

    private static final int MAX_SERIAL = Short.MAX_VALUE * 2 + 1;

    private static final Random random = new Random(System.currentTimeMillis());
    private static final AtomicInteger serial
            = new AtomicInteger((random.nextInt() & 0xFFFF) % MAX_SERIAL);

    public static int nextSerialNum() {
        for (;;) {
            int current = serial.get();
            int next = current + 1;
            next = next == MAX_SERIAL + 1 ? 0 : next;
            if (serial.compareAndSet(current, next))
                return next;
        }
    }

    private static Packet EMPTY = createEmpty();

    public static Packet empty() { return EMPTY; }

    private static Packet createEmpty() {
        Packet packet = new Packet();
        packet.setAttributes(Collections.emptyList());
        return packet;
    }

    public void addAttribute(Enums.Attribute type, byte[] value) {
        attributes.add(new Attribute(type.code(), value));
    }

    public byte[] getAttribute(Enums.Attribute type) {
        for (Attribute attribute : attributes) {
            if (attribute.getType() == type.code())
                return attribute.getValue();
        }
        return null;
    }

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
            return "Attribute{" +
                    "length=" + length +
                    ", type=" + type +
                    ", value=" + Arrays.toString(value) +
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
                ", ip=" + Arrays.toString(ip) +
                ", port=" + port +
                ", error=" + error +
                ", attrs=" + attrs +
                ", authenticator=" + Arrays.toString(authenticator) +
                ", attributes=" + attributes +
                '}';
    }
}
