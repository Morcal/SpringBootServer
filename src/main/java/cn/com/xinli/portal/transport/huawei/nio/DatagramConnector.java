package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.nio.CodecFactory;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.TransportUtils;
import cn.com.xinli.portal.transport.huawei.ConnectorHandler;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.Packet;
import cn.com.xinli.portal.transport.huawei.support.AbstractConnector;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Datagram portal connector.
 *
 * <p>This class implements portal connector via underlying datagram sockets.
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public final class DatagramConnector extends AbstractConnector {
    /** Logger. */
    private final Logger logger = getLogger(DatagramConnector.class);

    public DatagramConnector(Endpoint endpoint,
                             CodecFactory<Packet> codecFactory,
                             ConnectorHandler handler)
            throws TransportException {
        super(endpoint, codecFactory, handler);
    }

    @Override
    public Optional<Packet> request(Packet request) throws IOException {
        DatagramSocket socket = null;

        try {
            /* Send request to remote. */
            socket = new DatagramSocket();
            socket.setSoTimeout(10_000);
            ByteBuffer buffer = codecFactory.getEncoder().encode(request, endpoint.getSharedSecret());

            DatagramPacket out = new DatagramPacket(
                    buffer.array(), buffer.remaining(), endpoint.getAddress(), endpoint.getPort());
            socket.send(out);

            if (logger.isTraceEnabled()) {
                logger.trace("SEND {{}}", TransportUtils.bytesToHexString(out.getData()));
            }

            /* Try to receive from remote. */
            int capacity = 1024;
            byte[] buf = new byte[capacity];
            DatagramPacket response = new DatagramPacket(buf, buf.length);
            socket.receive(response);

            /* Decode response. */
            buffer.clear();
            buffer.put(response.getData(), 0, response.getLength());
            buffer.flip();
            Packet responsePacket = codecFactory.getDecoder()
                    .decode(request.getAuthenticator(), buffer, endpoint.getSharedSecret());
            return Optional.ofNullable(responsePacket);
        } catch (SocketTimeoutException e) {
            logger.warn("* Receive from endpoint timeout, endpoint: {}, request: {}",
                    endpoint, request);
            return Optional.empty();
        } finally {
            if (socket != null) {
                socket.close();
                socket.disconnect();
            }
        }
    }

    @Override
    protected void send(Packet packet) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        ByteBuffer buffer = codecFactory.getEncoder()
                .encode(packet, endpoint.getSharedSecret());

        DatagramPacket pack = new DatagramPacket(
                buffer.array(), buffer.remaining(), endpoint.getAddress(), endpoint.getPort());
        socket.send(pack);
        socket.close();
    }
}
