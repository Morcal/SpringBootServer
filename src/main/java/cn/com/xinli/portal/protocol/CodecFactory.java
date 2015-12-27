package cn.com.xinli.portal.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Portal codec factory.
 * <p>
 * Classes implements this interface should be thread-safe.
 * <p>
 * Project: xpws
 *
 * @param <T> packet type extends {@link Packet}.
 *
 * @author zhoupeng 2015/12/22.
 */
public interface CodecFactory<T> {
    /**
     * Datagram decoder.
     */
    interface DatagramDecoder<T> {
        /**
         * Decode incoming request datagram packet.
         * @param in incoming request datagram packet.
         * @param sharedSecret shared secret.
         * @return decoded packet.
         */
        T decode(ByteBuffer in, String sharedSecret) throws IOException;

        /**
         * Decode incoming response datagram packet.
         * @param authenticator original request authenticator.
         * @param in incoming response datagram packet.
         * @param sharedSecret shared secret.
         * @return decoded packet.
         * @throws IOException
         */
        T decode(byte[] authenticator, ByteBuffer in, String sharedSecret) throws IOException;
    }

    /**
     * Datagram encoder.
     */
    interface DatagramEncoder<T> {
        /**
         * Encode outgoing request datagram packet.
         * @param packet packet.
         * @param sharedSecret shared secret.
         * @return outgoing datagram packet buffer.
         * @throws IOException
         */
        ByteBuffer encode(T packet, String sharedSecret) throws IOException;

        /**
         * Encode outgoing response datagram packet.
         * @param authenticator original request authenticator.
         * @param packet packet to send.
         * @param sharedSecret shared secret.
         * @return outgoing datagram packet buffer.
         * @throws IOException
         */
        ByteBuffer encode(byte[] authenticator, T packet, String sharedSecret) throws IOException;
    }

    /**
     * Get datagram decoder.
     *
     * @return datagram decoder.
     */
    DatagramDecoder<T> getDecoder();

    /**
     * Get datagram encoder.
     *
     * @return datagram encoder.
     */
    DatagramEncoder<T> getEncoder();
}
