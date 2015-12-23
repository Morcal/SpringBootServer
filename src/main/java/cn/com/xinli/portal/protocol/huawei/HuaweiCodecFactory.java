package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Packet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Huawei portal protocol codec factory.
 * <p>
 * <p>{@link Decoder} and {@link Encoder} should be thread-safe.</p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class HuaweiCodecFactory implements CodecFactory {
    /**
     * Log.
     */
    private static final Log log = LogFactory.getLog(HuaweiCodecFactory.class);

    /**
     * Decoder.
     */
    private final DatagramDecoder decoder = new Decoder();

    /**
     * Encoder.
     */
    private final DatagramEncoder encoder = new Encoder();

    @Override
    public DatagramDecoder getDecoder() {
        return decoder;
    }

    @Override
    public DatagramEncoder getEncoder() {
        return encoder;
    }

    /**
     * Calculate authenticator.
     * <p>
     *     <code>Authenticator = MD5(16bytes + 16bytes of 0 + attributes + shared secret)</code>
     * </p>
     * @param output output stream.
     * @param sharedSecret shared secret.
     * @return 16 bytes authenticator.
     * @throws IOException
     */
    static byte[] calculateAuthenticator(ByteArrayOutputStream output, String sharedSecret) throws IOException {
        ByteArrayOutputStream cal = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(cal)) {
            if (StringUtils.isEmpty(sharedSecret)) {
                log.warn("+ empty shared secret.");
            } else {
                dos.write(output.toByteArray());
                dos.write(sharedSecret.getBytes());
                dos.flush();
            }

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(cal.toByteArray());
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Read bytes from input stream.
     * @param input input stream.
     * @param bytes bytes to read.
     * @return bytes read.
     * @throws IOException
     */
    byte[] readBytes(InputStream input, int bytes) throws IOException {
        byte[] data = new byte[bytes];
        if (input.read(data, 0, data.length) != bytes) {
            throw new IOException("not enough data.");
        }
        return data;
    }

    /**
     * Read {@link Packet.Attribute}s from input stream.
     * @param input input stream.
     * @param size attribute size.
     * @return collection of {@link Packet.Attribute}s.
     * @throws IOException
     */
    Collection<Packet.Attribute> readAttributes(InputStream input, int size) throws IOException {
        if (size == 0) {
            return Collections.emptyList();
        }

        List<Packet.Attribute> attributes = new ArrayList<>();
        while (size-- > 0) {
            int type = input.read() & 0xFF;
            int length = input.read() & 0xFF;
            attributes.add(new Packet.Attribute(type, readBytes(input, length - 2)));
        }

        return attributes;
    }

    /**
     * Verify request authenticator.
     *
     * @param in           incoming packet.
     * @param sharedSecret shared secret.
     * @return true if incoming packet is valid.
     * @throws IOException
     */
    public boolean verify(DatagramPacket in, String sharedSecret) throws IOException {
        return verify(null, in, sharedSecret);
    }

    /**
     * Verify response by authenticator.
     *
     * @param in           incoming packet.
     * @param sharedSecret shared secret.
     * @return true if incoming packet is valid.
     * @throws IOException
     */
    public boolean verify(byte[] authenticator,
                          DatagramPacket in,
                          String sharedSecret) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] data = Arrays.copyOfRange(in.getData(), 0, in.getLength());
        if (data.length < 32) {
            return false; // not enough size.
        }

        try (DataOutputStream dos = new DataOutputStream(bao)) {
            dos.write(data, 0 , 16);
            if (authenticator == null) {
                /* incoming request. */
                dos.writeLong(0);
                dos.writeLong(0);
            } else {
                dos.write(authenticator);
            }
            if (data.length > 32) {
                dos.write(data, 32, data.length - 32);
            }
            dos.flush();
        }

        return Arrays.equals(calculateAuthenticator(bao, sharedSecret),
                Arrays.copyOfRange(data, 16, 32));
    }

    class Encoder implements CodecFactory.DatagramEncoder {

        /**
         * Write attributes from a {@link Packet} to a {@link DataOutputStream}.
         * @param output output stream.
         * @param attributes attributes.
         * @throws IOException
         */
        void writeAttributes(DataOutputStream output,
                             Collection<Packet.Attribute> attributes) throws IOException {
            for (Packet.Attribute attribute : attributes) {
                output.writeByte(attribute.getType());
                output.writeByte(attribute.getLength());
                output.write(attribute.getValue());
            }
        }

        /**
         * Write authenticator to a {@link ByteArrayOutputStream}.
         * @param bao output stream.
         * @param authenticator authentiator to write.
         * @throws IOException
         */
        void writeAuthenticator(ByteArrayOutputStream bao, byte[] authenticator) throws IOException {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            byte[] array = Arrays.copyOf(bao.toByteArray(), bao.size());
            try (DataOutputStream authDos = new DataOutputStream(o)) {
                authDos.write(array, 0, 16);
                authDos.write(authenticator);
                authDos.write(array, 32, array.length - 32);
                authDos.flush();
            }
            bao.reset();
            o.writeTo(bao);
        }

        /**
         * Write packet to datagram.
         * @param packet packet.
         * @param server remote server address.
         * @param port remote server port.
         * @param sharedSecret shared secret.
         * @param authenticator authenticator.
         * @return datagram packet.
         * @throws IOException
         */
        DatagramPacket writePacket(Packet packet,
                                   InetAddress server,
                                   int port,
                                   String sharedSecret,
                                   byte[] authenticator) throws IOException {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(bao)) {
                output.writeByte(packet.getVersion());
                output.writeByte(packet.getType());
                output.writeByte(packet.getAuthType());
                output.writeByte(packet.getReserved());
                output.writeShort(packet.getSerialNum());
                output.writeShort(packet.getReqId());
                assert packet.getIp().length == 4;
                output.write(packet.getIp());
                output.writeShort(packet.getPort());
                output.writeByte(packet.getError());
                output.writeByte(packet.getAttrs());
                if (packet.getVersion() == V2.Version) {
                    if (authenticator != null) {
                        /* Write original request's authenticator. */
                        output.write(authenticator);
                    } else {
                        /* Write 16 bytes of 0 placeholder. */
                        output.writeLong(0);
                        output.writeLong(0);
                    }
                }
                writeAttributes(output, packet.getAttributes());
                output.flush();

                if (packet.getVersion() == V2.Version) {
                    /* Calculate new authenticator. */
                    byte[] authen = calculateAuthenticator(bao, sharedSecret);
                    if (authen.length < 1) {
                        throw new RuntimeException("Invalid calculated authenticator.");
                    }
                    packet.setAuthenticator(authen);
                    writeAuthenticator(bao, authen);
                }
                return new DatagramPacket(bao.toByteArray(), bao.size(), server, port);
            }
        }

        @Override
        public DatagramPacket encode(Packet packet,
                                     InetAddress server,
                                     int port,
                                     String sharedSecret) throws IOException {
            return writePacket(packet, server, port, sharedSecret, null);
        }

        @Override
        public DatagramPacket encode(byte[] authenticator,
                                     Packet packet,
                                     InetAddress server,
                                     int port,
                                     String sharedSecret) throws IOException {
            return writePacket(packet, server, port, sharedSecret, authenticator);
        }
    }

    class Decoder implements CodecFactory.DatagramDecoder {

        /**
         * Read {@link Packet} from input stream.
         * @param input input stream.
         * @return Packet.
         * @throws IOException
         */
        private Packet readPacket(InputStream input) throws IOException {
            Packet packet = new Packet();
            packet.setVersion(input.read() & 0xFF);
            packet.setType(input.read() & 0xFF);
            packet.setAuthType(input.read() & 0xFF);
            packet.setReserved(input.read() & 0xFF);
            packet.setSerialNum(((input.read() & 0xff) << 8) | (input.read() & 0xff));
            packet.setReqId(((input.read() & 0xff) << 8) | (input.read() & 0xff));
            packet.setIp(readBytes(input, 4));
            byte[] port = readBytes(input, 2);
            packet.setPort(port[0] << 8 | port[1]);
            packet.setError(input.read() & 0xFF);
            packet.setAttrs(input.read() & 0xFF);
            packet.setAuthenticator(readBytes(input, 16));
            packet.setAttributes(readAttributes(input, packet.getAttrs()));
            return packet;
        }

        @Override
        public Packet decode(DatagramPacket in, String sharedSecret) throws IOException {
            return decode(null, in, sharedSecret);
        }

        @Override
        public Packet decode(byte[] authenticator,
                             DatagramPacket in,
                             String sharedSecret) throws IOException {
            ByteArrayInputStream bai = new ByteArrayInputStream(in.getData(), 0, in.getLength());
            int ver = (int)Arrays.copyOfRange(in.getData(), 0, 1)[0];

            if (ver == V2.Version) {
                if (!verify(authenticator, in, sharedSecret)) {
                    log.warn("Invalid Huawei portal packet received.");
                    return null;
                }
            }

            try (InputStream input = new BufferedInputStream(bai)) {
                return readPacket(input);
            }
        }
    }
}