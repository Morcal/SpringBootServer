package cn.com.xinli.portal.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * Portal codec factory.
 * <p>
 * <p>Classes implements this interface should provide
 * thread-safe implementations for {@link DatagramEncoder} and
 * {@link DatagramDecoder}.</p>
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface CodecFactory {
    /**
     * Datagram decoder.
     */
    interface DatagramDecoder {
        /**
         * Decode incoming request datagram packet.
         * @param in incoming request datagram packet.
         * @param sharedSecret shared secret.
         * @return decoded packet.
         */
        Packet decode(DatagramPacket in, String sharedSecret) throws IOException;

        /**
         * Decode incoming response datagram packet.
         * @param authenticator original request authenticator.
         * @param in incoming response datagram packet.
         * @param sharedSecret shared secret.
         * @return decoded packet.
         * @throws IOException
         */
        Packet decode(byte[] authenticator, DatagramPacket in, String sharedSecret) throws IOException;
    }

    /**
     * Datagram encoder.
     */
    interface DatagramEncoder {
        /**
         * Encode outgoing request datagram packet.
         * @param packet packet.
         * @param server remote server address.
         * @param port remote server port.
         * @param sharedSecret shared secret.
         * @return outgoing datagram packet.
         * @throws IOException
         */
        DatagramPacket encode(Packet packet, InetAddress server, int port, String sharedSecret) throws IOException;

        /**
         * Encode outgoing response datagram packet.
         * @param authenticator original request authenticator.
         * @param packet packet to send.
         * @param server remote server address.
         * @param port remote server port.
         * @param sharedSecret shared secret.
         * @return outgoing datagram packet.
         * @throws IOException
         */
        DatagramPacket encode(byte[] authenticator, Packet packet, InetAddress server, int port, String sharedSecret) throws IOException;
    }

    /**
     * Get datagram decoder.
     *
     * @return datagram decoder.
     */
    DatagramDecoder getDecoder();

    /**
     * Get datagram encoder.
     *
     * @return datagram encoder.
     */
    DatagramEncoder getEncoder();
}
