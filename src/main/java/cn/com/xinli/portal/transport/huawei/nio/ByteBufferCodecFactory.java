package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.nio.CodecFactory;
import cn.com.xinli.portal.transport.huawei.Packet;
import cn.com.xinli.portal.transport.huawei.Packets;
import cn.com.xinli.portal.transport.huawei.Version;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * HUAWEI portal protocol codec factory.
 *
 * <p>{@link Decoder} and {@link Encoder} should be thread-safe.
 *
 * <p>This implementation is stateless and thread-safe.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public final class ByteBufferCodecFactory implements CodecFactory<Packet> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(ByteBufferCodecFactory.class);

    /** Decoder. */
    private final DatagramDecoder<Packet> decoder = new Decoder();

    /** Encoder. */
    private final DatagramEncoder<Packet> encoder = new Encoder();

    @Override
    public DatagramDecoder<Packet> getDecoder() {
        return decoder;
    }

    @Override
    public DatagramEncoder<Packet> getEncoder() {
        return encoder;
    }

    /**
     * Calculate authenticator.
     *
     * <p><code>Authenticator = MD5(16bytes + 16bytes of 0 + attributes + shared secret)</code>
     *
     * @param data data.
     * @param authenticator original request authenticator.
     * @param sharedSecret shared secret.
     * @return 16 bytes authenticator.
     */
    private static byte[] calculateAuthenticator(byte[] data, byte[] authenticator, String sharedSecret) {
        if (data == null || data.length < 32) {
            return new byte[0];
        }

        if (authenticator != null && authenticator.length != 16) {
            return new byte[0];
        }

        ByteBuffer buf = ByteBuffer.allocate(Packet.MAX_LENGTH);
        buf.put(data, 0, 16);

        if (authenticator != null) {
            buf.put(authenticator);
        } else {
            buf.putLong(0L);
            buf.putLong(0L);
        }

        if (data.length > 32) {
            buf.put(data, 32, data.length - 32);
        }

        if (!StringUtils.isEmpty(sharedSecret)) {
            buf.put(sharedSecret.getBytes());
        }

        buf.flip();

        return Packets.md5sum(Arrays.copyOfRange(buf.array(), 0, buf.remaining()));
    }

    /**
     * Read bytes from input stream.
     *
     * @param buffer input buffer.
     * @param bytes bytes to receive.
     * @return bytes receive.
     */
    private static byte[] readBytes(ByteBuffer buffer, int bytes) {
        byte[] data = new byte[bytes];
        buffer.get(data);
        return data;
    }

    /**
     * Read {@link Packet.Attribute}s from input stream.
     *
     * @param buffer input buffer.
     * @param size attribute size.
     * @return collection of {@link Packet.Attribute}s.
     */
    private static Collection<Packet.Attribute> readAttributes(ByteBuffer buffer, int size) {
        if (size == 0) {
            return Collections.emptyList();
        }

        List<Packet.Attribute> attributes = new ArrayList<>();
        while (size-- > 0) {
            int type = buffer.get() & 0xFF;
            int length = buffer.get() & 0xFF;
            attributes.add(new Packet.Attribute(type, readBytes(buffer, length - 2)));
        }

        return attributes;
    }

    /**
     * Verify request authenticator.
     *
     * @param buffer incoming packet.
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
     * @param buffer incoming packet.
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

    /** HUAWEI portal protocol encoder. */
    class Encoder implements CodecFactory.DatagramEncoder<Packet> {
        /**
         * Write attributes from a {@link Packet} to a {@link ByteBuffer}.
         *
         * @param buffer output buffer.
         * @param attributes attributes.
         */
        private void writeAttributes(ByteBuffer buffer,
                             Collection<Packet.Attribute> attributes) {
            for (Packet.Attribute attribute : attributes) {
                buffer.put((byte) attribute.getType());
                buffer.put((byte) attribute.getLength());
                buffer.put(attribute.getValue());
            }
        }

        /**
         * Write authenticator to a {@link ByteBuffer}.
         *
         * @param buffer output buffer.
         * @param authenticator authenticator to write.
         */
        private void writeAuthenticator(ByteBuffer buffer, byte[] authenticator) {
            if (authenticator == null || authenticator.length != 16) {
                throw new IllegalArgumentException("Invalid authenticator.");
            }

            if (buffer.limit() < 32) {
                throw new IllegalArgumentException("Not enough space buffer buffer.");
            }

            System.arraycopy(authenticator, 0, buffer.array(), 16, 16);
        }

        /**
         * Write packet to buffer.
         * @param packet packet to write.
         * @param sharedSecret shared secret.
         * @return byte buffer.
         */
        private ByteBuffer writePacket(Packet packet, String sharedSecret) {
            return writePacket(packet, sharedSecret, null);
        }

        /**
         * Write packet to buffer.
         *
         * @param packet packet to write.
         * @param sharedSecret shared secret.
         * @param authenticator authenticator.
         * @return byte buffer.
         */
        private ByteBuffer writePacket(Packet packet, String sharedSecret, byte[] authenticator) {
            assert packet.getIp().length == 4;
            ByteBuffer buffer = ByteBuffer.allocate(Packet.MAX_LENGTH);
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
                byte[] result = calculateAuthenticator(
                        Arrays.copyOfRange(buffer.array(), 0, buffer.position()),
                        authenticator,
                        sharedSecret);
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
        public ByteBuffer encode(Packet packet,
                                 String sharedSecret) {
            return writePacket(packet, sharedSecret);
        }

        @Override
        public ByteBuffer encode(byte[] authenticator,
                                 Packet packet,
                                 String sharedSecret) {
            if (authenticator != null && authenticator.length != 16) {
                throw new IllegalArgumentException("Invalid authenticator.");
            }
            return writePacket(packet, sharedSecret, authenticator);
        }
    }

    /** HUAWEI portal protocol decoder. */
    class Decoder implements CodecFactory.DatagramDecoder<Packet> {

        /**
         * Read {@link Packet} from input stream.
         *
         * @param buffer input buffer.
         * @return Packet.
         */
        private Packet readPacket(ByteBuffer buffer) {
            byte[] ip = new byte[4];
            Packet packet = new Packet();
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
        public Packet decode(ByteBuffer in, String sharedSecret) {
            return decode(null, in, sharedSecret);
        }

        @Override
        public Packet decode(byte[] authenticator,
                             ByteBuffer buffer,
                             String sharedSecret) {
            int ver = (int) buffer.get();

            if (ver == Version.V2.value()) {
                buffer.rewind();
                if (!verify(authenticator, buffer, sharedSecret)) {
                    ByteBufferCodecFactory.this.logger.warn("Invalid HUAWEI portal packet received.");
                    return null;
                }
            }

            return readPacket(buffer);
        }
    }
}