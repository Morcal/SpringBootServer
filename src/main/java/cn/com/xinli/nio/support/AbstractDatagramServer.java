package cn.com.xinli.nio.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Abstract datagram server.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/25.
 */
public abstract class AbstractDatagramServer {
    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(AbstractDatagramServer.class);

    /** Server datagram channel. */
    protected DatagramChannel channel;

    /** Server datagram channel selector. */
    private Selector selector;

    /** Shutdown indicator. */
    private volatile boolean shutdown = false;

    /** Server listen port. */
    protected final int port;

    /** Internal executor service. */
    private ExecutorService executorService;

    /**
     * Verify incoming packet.
     * @param buffer incoming datagram buffer.
     * @return true if packet is valid.
     * @throws IOException
     */
    protected abstract boolean verifyPacket(ByteBuffer buffer) throws IOException;

    /**
     * Handle incoming datagram packet.
     * @param buffer incoming packet.
     */
    protected abstract void handlePacket(ByteBuffer buffer, SocketAddress remote);

    /**
     * Create datagram server socket receive buffer.
     * @return receive byte buffer.
     */
    protected abstract ByteBuffer createReceiveBuffer();

    /**
     * Receive incoming datagram packet from channel.
     * @param channel incoming datagram channel.
     * @param buffer receive buffer.
     * @return remote socket address.
     * @throws IOException
     */
    protected SocketAddress receive(DatagramChannel channel, ByteBuffer buffer) throws IOException {
        return channel.receive(buffer);
    }

    public AbstractDatagramServer(int port, int threadSize, String threadName) {
        this(port, Executors.newFixedThreadPool(threadSize, r -> new Thread(r, threadName)));
    }

    public AbstractDatagramServer(int port, ExecutorService executorService) {
        this.port = port;
        this.executorService = executorService;
    }

    /**
     * Start datagram server loop.
     */
    private void startLoop() {
        while (!shutdown) {
            try {
                int selected = selector.select(1000L);
                if (selected < 1)
                    continue;

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        DatagramChannel ch = (DatagramChannel) key.channel();
                        assert ch == channel;
                        ByteBuffer buffer = createReceiveBuffer();
                        SocketAddress remote = receive(ch, buffer);
                        buffer.flip();

                        if (!verifyPacket(buffer)) {
                            logger.warn("* Invalid portal request, dropped.");
                            continue;
                        }
                        buffer.rewind();
                        executorService.submit(() -> handlePacket(buffer, remote));
                    }
                }

            } catch (IOException e) {
                // no-op.
                if (logger.isDebugEnabled()) {
                    logger.debug("Datagram Server error", e);
                }
            }
        }
    }

    /**
     * Start datagram server.
     * @throws IOException
     */
    public void start() throws IOException {
        channel = DatagramChannel.open();
        channel.configureBlocking(false);
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_READ);
        channel.socket().bind(new InetSocketAddress(port));
        executorService.submit(this::startLoop);
    }

    /**
     * Shutdown datagram server.
     */
    public void shutdown() {
        shutdown = true;
        logger.info("Shutting down datagram server...");
        selector.wakeup();

        try {
            channel.close();
            Thread.sleep(100);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            logger.error("Datagram server shutdown error", e);
        } finally {
            executorService.shutdownNow();
        }

        logger.info("Datagram server quit.");
    }
}
