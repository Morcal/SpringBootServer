package cn.com.xinli.radius;

import cn.com.xinli.nio.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusCodecFactory implements CodecFactory<RadiusPacket> {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(RadiusCodecFactory.class);

    /** Decoder. */
    private final DatagramDecoder<RadiusPacket> decoder = new Decoder();

    /** Encoder. */
    private final DatagramEncoder<RadiusPacket> encoder = new Encoder();

    @Override
    public DatagramDecoder<RadiusPacket> getDecoder() {
        return null;
    }

    @Override
    public DatagramEncoder<RadiusPacket> getEncoder() {
        return null;
    }

    /** RADIUS protocol decoder. */
    class Decoder implements CodecFactory.DatagramDecoder<RadiusPacket> {

        @Override
        public RadiusPacket decode(ByteBuffer in, String sharedSecret) throws IOException {
            return null;
        }

        @Override
        public RadiusPacket decode(byte[] authenticator, ByteBuffer in, String sharedSecret) throws IOException {
            return null;
        }
    }


    /** HUAWEI portal protocol encoder. */
    class Encoder implements CodecFactory.DatagramEncoder<RadiusPacket> {
        @Override
        public ByteBuffer encode(RadiusPacket packet, String sharedSecret) throws IOException {
            return null;
        }

        @Override
        public ByteBuffer encode(byte[] authenticator, RadiusPacket packet, String sharedSecret) throws IOException {
            return null;
        }
    }
}