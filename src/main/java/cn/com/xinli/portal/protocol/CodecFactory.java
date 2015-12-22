package cn.com.xinli.portal.protocol;

import java.io.IOException;
import java.net.DatagramPacket;

/**
 * Portal codec factory.
 *
 * <p>Classes implements this interface should provide
 * thread-safe implementations for {@link DatagramEncoder} and
 * {@link DatagramDecoder}.</p>
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public interface CodecFactory {
    /**
     * Datagram decoder.
     */
    interface DatagramDecoder {
        Packet decode(DatagramPacket in, String sharedSecret) throws IOException;
    }

    /**
     * Datagram encoder.
     */
    interface DatagramEncoder {
        DatagramPacket encode(Packet packet, String sharedSecret) throws IOException;
    }

    /**
     * Get datagram decoder.
     * @return datagram decoder.
     */
    DatagramDecoder getDecoder();

    /**
     * Get datagram encoder.
     * @return datagram encoder.
     */
    DatagramEncoder getEncoder();
}
