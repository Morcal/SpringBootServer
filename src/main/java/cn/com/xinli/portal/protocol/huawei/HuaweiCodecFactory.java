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
 *
 * <p>{@link Decoder} and {@link Encoder} should be thread-safe.</p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class HuaweiCodecFactory implements CodecFactory {
    /** Log. */
    private static final Log log = LogFactory.getLog(HuaweiCodecFactory.class);

    /** Supported Huawei portal version. */
    private final int version;

    /** Decoder. */
    private final DatagramDecoder decoder = new Decoder();

    /** Encoder. */
    private final DatagramEncoder encoder = new Encoder();

    public HuaweiCodecFactory(int version) {
        this.version = version;
    }

    @Override
    public DatagramDecoder getDecoder() {
        return decoder;
    }

    @Override
    public DatagramEncoder getEncoder() {
        return encoder;
    }

    static byte[] calculateAuthenticator(ByteArrayOutputStream output, String sharedSecret) throws IOException {
        try {
            if (StringUtils.isEmpty(sharedSecret)) {
                log.warn("+ empty shared secret.");
            } else {
                DataOutputStream dos = new DataOutputStream(output);
                dos.write(sharedSecret.getBytes());
                dos.flush();
            }

            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(output.toByteArray());
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
            return new byte[0];
        }
    }

    byte[] readBytes(InputStream input, int bytes) throws IOException {
        byte[] data = new byte[bytes];
        if (input.read(data) != bytes) {
            throw new IOException("not enough data.");
        }
        return data;
    }

    Collection<Packet.Attribute> readAttributes(InputStream input, int size) throws IOException {
        if (size == 0) {
            return Collections.emptyList();
        }

        List<Packet.Attribute> attributes = new ArrayList<>();
        while (size-- > 0) {
            int type = input.read() & 0xFF;
            int length = input.read() & 0xFF;
            attributes.add(new Packet.Attribute(type, readBytes(input, length)));
        }

        return attributes;
    }

    /**
     * Verify authenticator.
     * @param packet incoming packet.
     * @param sharedSecret shared secret.
     * @return true if incoming packet is valid.
     * @throws IOException
     */
    public boolean verify(Packet packet, String sharedSecret) throws IOException {
        DatagramPacket pkt = encoder.encode(packet, sharedSecret);
        byte[] dst = new byte[16];
        System.arraycopy(pkt.getData(), 16, dst, 0, 16);
        return Arrays.equals(packet.getAuthenticator(), dst);
    }

    class Encoder implements CodecFactory.DatagramEncoder {

        void writeAttributes(DataOutputStream output,
                                    Collection<Packet.Attribute> attributes) throws IOException {
            for (Packet.Attribute attribute : attributes) {
                output.writeByte(attribute.getType());
                output.writeByte(attribute.getLength());
                output.write(attribute.getValue());
            }
        }

        @Override
        public DatagramPacket encode(Packet packet, String sharedSecret) throws IOException {
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            try (DataOutputStream output = new DataOutputStream(bao)) {
                output.writeByte(version);
                output.writeByte(packet.getType());
                output.writeByte(packet.getAuthType());
                output.writeByte(packet.getReserved());
                output.writeShort(packet.getSerialNum());
                output.writeShort(packet.getReqId());
                output.write(packet.getAddress().getAddress());
                output.writeShort(packet.getPort());
                output.writeByte(packet.getError());
                output.writeByte(packet.getAttrs());
                if (version == V2.Version) {
                    /* Write 16 byte of 0. */
                    output.writeLong(0);
                    output.writeLong(0);
                }
                writeAttributes(output, packet.getAttributes());
                output.flush();

                if (version == V2.Version) {
                    byte[] authenticator = calculateAuthenticator(bao, sharedSecret);
                    if (authenticator.length > 0) {
                        output.write(authenticator, 16, authenticator.length);
                    }
                }
                output.flush();

                return new DatagramPacket(bao.toByteArray(), bao.size());
            }
        }
    }

    class Decoder implements CodecFactory.DatagramDecoder {

        @Override
        public Packet decode(DatagramPacket in, String sharedSecret) throws IOException {
            ByteArrayInputStream bai = new ByteArrayInputStream(in.getData());
            try (InputStream input = new BufferedInputStream(bai)) {
                Packet packet = new Packet();
                packet.setVersion(input.read() & 0xFF);
                packet.setType(input.read() & 0xFF);
                packet.setAuthType(input.read() & 0xFF);
                packet.setReserved(input.read() & 0xFF);
                packet.setSerialNum(((input.read() & 0xff) << 8) | (input.read() & 0xff));
                packet.setReqId(((input.read() & 0xff) << 8) | (input.read() & 0xff));
                packet.setAddress(InetAddress.getByAddress(readBytes(input, 4)));
                byte[] port = readBytes(input, 2);
                packet.setPort(port[0] << 8 | port[1]);
                packet.setError(input.read() & 0xFF);
                packet.setAuthenticator(readBytes(input, 16));
                packet.setAttrs(input.read() & 0xFF);
                packet.setAttributes(readAttributes(input, packet.getAttrs()));

                if (version == V2.Version) {
                    if (!verify(packet, sharedSecret)) {
                        log.warn("Invalid Huawei portal packet received.");
                        return null;
                    }
                }

                return packet;
            }
        }
    }
}
