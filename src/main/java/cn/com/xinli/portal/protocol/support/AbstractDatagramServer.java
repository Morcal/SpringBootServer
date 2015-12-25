package cn.com.xinli.portal.protocol.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/25.
 */
public abstract class AbstractDatagramServer {
    /** Log. */
    private static final Log log = LogFactory.getLog(AbstractDatagramServer.class);

    protected DatagramSocket socket;

    private volatile boolean shutdown = false;

    protected final int port;

    /** Default worker thread size. */
    private static final int DEFAULT_THREAD_SIZE = 4;

    private ExecutorService executorService;

    protected abstract boolean verifyPacket(DatagramPacket packet) throws IOException;

    protected abstract void handlePacket(DatagramSocket socket, DatagramPacket packet);

    public AbstractDatagramServer(int port) {
        this(port, DEFAULT_THREAD_SIZE);
    }

    public AbstractDatagramServer(int port, int threadSize) {
        this.port = port;
        executorService = Executors.newFixedThreadPool(threadSize);
    }

    private void startLoop() {
        while (!shutdown) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet); /* This would be blocking. */
                if (!verifyPacket(packet)) {
                    log.warn("* Invalid portal request, dropped.");
                    continue;
                }

                executorService.submit(() -> handlePacket(socket, packet));
            } catch (IOException e) {
                // no-op.
                if (log.isDebugEnabled()) {
                    log.debug(e);
                }
            }
        }
    }

    public void start() throws IOException {
        socket = new DatagramSocket(port);
        executorService.submit(this::startLoop);
    }

    public void shutdown() {
        shutdown = true;
        try {
            Thread.sleep(100);
            executorService.shutdown();
            executorService.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            executorService.shutdownNow();
            socket.close();
        }
    }
}
