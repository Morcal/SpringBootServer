package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.CodecFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * Huawei portal protocol codec factory.
 *
 * <p>{@link Decoder} and {@link Encoder} should be thread-safe.
 *
 * <p>This implementation is stateless and thread-safe.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
final class HuaweiCodecFactory implements CodecFactory<HuaweiPacket> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(HuaweiCodecFactory.class);

    /** Decoder. */
    private final DatagramDecoder<HuaweiPacket> decoder = new Decoder();

    /** Encoder. */
    private final DatagramEncoder<HuaweiPacket> encoder = new Encoder();

    @Override
    public DatagramDecoder<HuaweiPacket> getDecoder() {
        return decoder;
    }

    @Override
    public DatagramEncoder<HuaweiPacket> getEncoder() {
        return encoder;
    }

    /**
     * Calculate authenticator.
     * @param buffer content buffer.
     * @param authenticator authenticator.
     * @param sharedSecret shared secret.
     * @return authenticator.
     */
    static byte[] calculateAuthenticator(ByteBuffer buffer, byte[] authenticator, String sharedSecret) {
        if (!buffer.hasArray())
            return new byte[0];

        byte[] data = Arrays.copyOfRange(buffer.array(), 0, buffer.remaining());
        return calculateAuthenticator(data, authenticator, sharedSecret);
    }

    /**
     * Calculate authenticator.
     *
     * <p><code>Authenticator = MD5(16bytes + 16bytes of 0 + attributes + shared secret)</code>
     *
     * @param data          data.
     * @param authenticator original request authenticator.
     * @param sharedSecret shared secret.
     * @return 16 bytes authenticator.
     */
    static byte[] calculateAuthenticator(byte[] data, byte[] authenticator, String sharedSecret) {
        if (data == null || data.length < 32) {
            return new byte[0];
        }

        if (authenticator != null && authenticator.length != 16) {
            return new byte[0];
        }

        ByteBuffer buf = ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
        buf.put(data, 0, 16);

        if (authenticator != null) {
            buf.put(authenticator);
        } else {
            buf.putLong(0L);
            buf.putLong(0L);
        }

        if (!StringUtils.isEmpty(sharedSecret)) {
            buf.put(sharedSecret.getBytes());
        }

        if (data.length > 32) {
            buf.put(data, 32, data.length - 32);
        }

        buf.flip();

        return Packets.md5sum(buf.array());
    }

    /**
     * Read bytes from input stream.
     *
     * @param buffer input buffer.
     * @param bytes bytes to receive.
     * @return bytes receive.
     */
    static byte[] readBytes(ByteBuffer buffer, int bytes) {
        byte[] data = new byte[bytes];
        buffer.get(data);
        return data;
    }

    /**
     * Read {@link HuaweiPacket.Attribute}s from input stream.
     *
     * @param buffer input buffer.
     * @param size  attribute size.
     * @return collection of {@link HuaweiPacket.Attribute}s.
     */
    static Collection<HuaweiPacket.Attribute> readAttributes(ByteBuffer buffer, int size) {
        if (size == 0) {
            return Collections.emptyList();
        }

        List<HuaweiPacket.Attribute> attributes = new ArrayList<>();
        while (size-- > 0) {
            int type = buffer.get() & 0xFF;
            int length = buffer.get() & 0xFF;
            attributes.add(new HuaweiPacket.Attribute(type, readBytes(buffer, length - 2)));
        }

        return attributes;
    }

    /**
     * Verify request authenticator.
     *
     * @param buffer           incoming packet.
     * @param sharedSecret shared secret.
     * @return true if incoming packet is valid.
     */
    public static boolean verify(ByteBuffer buffer, String sharedSecret) {
        if (!buffer.hasArray())
            return false;

        byte[] data = Arrays.copyOfRange(buffer.array(), 0, buffer.remaining());
        return data.length >= 32 &&
                Arrays.equals(calculateAuthenticator(
                        data, null, sharedSecret), Arrays.copyOfRange(data, 16, 32));
    }

    /**
     * Verify response by authenticator.
     *
     * @param authenticator original
     * @param buffer           incoming packet.
     * @param sharedSecret shared secret.
     * @return true if incoming packet is valid.
     */
    public static boolean verify(byte[] authenticator, ByteBuffer buffer, String sharedSecret) {
        if (!buffer.hasArray())
            return false;
        byte[] data = Arrays.copyOfRange(buffer.array(), 0, buffer.remaining());
        return data.length >= 32 &&
                Arrays.equals(calculateAuthenticator(
                        data, authenticator, sharedSecret), Arrays.copyOfRange(data, 16, 32));

    }

    /** Huawei portal protocol encoder. */
    class Encoder implements CodecFactory.DatagramEncoder<HuaweiPacket> {
        /**
         * Write attributes from a {@link HuaweiPacket} to a {@link DataOutputStream}.
         *
         * @param buffer     output buffer.
         * @param attributes attributes.
         */
        void writeAttributes(ByteBuffer buffer,
                             Collection<HuaweiPacket.Attribute> attributes) {
            for (HuaweiPacket.Attribute attribute : attributes) {
                buffer.put((byte) attribute.getType());
                buffer.put((byte) attribute.getLength());
                buffer.put(attribute.getValue());
            }
        }

        /**
         * Write authenticator to a {@link ByteArrayOutputStream}.
         *
         * @param buffer           output buffer.
         * @param authenticator authenticator to write.
         */
        void writeAuthenticator(ByteBuffer buffer, byte[] authenticator) {
            if (authenticator == null || authenticator.length != 16) {
                throw new IllegalArgumentException("Invalid authenticator.");
            }

            if (buffer.limit() < 32) {
                throw new IllegalArgumentException("Not enough space buffer buffer.");
            }

            System.arraycopy(authenticator, 0, buffer.duplicate().array(), 16, 16);
        }


        ByteBuffer writePacket(HuaweiPacket packet, String sharedSecret) {
            return writePacket(packet, sharedSecret, null);
        }

        /**
         * Write packet to datagram.
         *
         * @param packet           packet to write.
         * @param sharedSecret  shared secret.
         * @param authenticator authenticator.
         * @return datagram packet.
         */
        ByteBuffer writePacket(HuaweiPacket packet, String sharedSecret, byte[] authenticator) {
            assert packet.getIp().length == 4;
            ByteBuffer buffer = ByteBuffer.allocate(HuaweiPacket.MAX_LENGTH);
            buffer.put((byte) packet.getVersion());
            buffer.put((byte) packet.getType());
            buffer.put((byte) packet.getAuthType());
            buffer.put((byte) packet.getReserved());
            buffer.putShort((short) packet.getSerialNum());
            buffer.putShort(new Integer(packet.getReqId()).shortValue());
            buffer.put(packet.getIp());
            buffer.putShort((short) packet.getPort());
            buffer.put((byte) packet.getError());
            buffer.put((byte) packet.getAttrs());
            if (packet.getVersion() == Version.V2.value()) {
                /* Write 16 bytes of 0 placeholder. */
                buffer.putLong(0L);
                buffer.putLong(0L);
            }

            writeAttributes(buffer, packet.getAttributes());

            if (packet.getVersion() == Version.V2.value()) {
                /* Calculate authenticator. */
                byte[] result = calculateAuthenticator(buffer, authenticator, sharedSecret);
                if (result.length < 1) {
                    throw new RuntimeException("Invalid calculated authenticator.");
                }
                packet.setAuthenticator(result);
                writeAuthenticator(buffer, result);
            }

            buffer.flip();
            return buffer;
        }

        @Override
        public ByteBuffer encode(HuaweiPacket packet,
                                 String sharedSecret) {
            return writePacket(packet, sharedSecret);
        }

        @Override
        public ByteBuffer encode(byte[] authenticator,
                                 HuaweiPacket packet,
                                 String sharedSecret) {
            if (authenticator != null && authenticator.length != 16) {
                throw new IllegalArgumentException("Invalid authenticator.");
            }
            return writePacket(packet, sharedSecret, authenticator);
        }
    }

    /** Huawei portal protocol decoder. */
    class Decoder implements CodecFactory.DatagramDecoder<HuaweiPacket> {

        /**
         * Read {@link HuaweiPacket} from input stream.
         *
         * @param buffer input buffer.
         * @return HuaweiPacket.
         */
        private HuaweiPacket readPacket(ByteBuffer buffer) {
            byte[] ip = new byte[4];
            HuaweiPacket packet = new HuaweiPacket();
            packet.setVersion(buffer.get() & 0xFF);
            packet.setType(buffer.get() & 0xFF);
            packet.setAuthType(buffer.get() & 0xFF);
            packet.setReserved(buffer.get() & 0xFF);
            packet.setSerialNum(((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff));
            packet.setReqId(((buffer.get() & 0xff) << 8) | (buffer.get() & 0xff));
            buffer.get(ip);
            packet.setIp(ip);
            byte[] port = readBytes(buffer, 2);
            packet.setPort(port[0] << 8 | port[1]);
            packet.setError(buffer.get() & 0xFF);
            packet.setAttrs(buffer.get() & 0xFF);
            if (packet.getVersion() == Version.V2.value()) {
                packet.setAuthenticator(readBytes(buffer, 16));
            }
            packet.setAttributes(readAttributes(buffer, packet.getAttrs()));
            return packet;
        }

        @Override
        public HuaweiPacket decode(ByteBuffer in, String sharedSecret) {
            return decode(null, in, sharedSecret);
        }

        @Override
        public HuaweiPacket decode(byte[] authenticator,
                                   ByteBuffer buffer,
                                   String sharedSecret) {
            int ver = (int) buffer.get();

            if (ver == Version.V2.value()) {
                buffer.rewind();
                if (!verify(authenticator, buffer, sharedSecret)) {
                    HuaweiCodecFactory.this.logger.warn("Invalid Huawei portal packet received.");
                    return null;
                }
            }

            return readPacket(buffer);
        }
    }
}