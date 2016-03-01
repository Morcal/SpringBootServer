package cn.com.xinli.radius;

import cn.com.xinli.nio.support.AbstractDatagramServer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
  * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/27.
 */
public class RadiusServer {
    AuthenticationServer authenticationServer;
    AccountingServer accountingServer;

    public RadiusServer(RadiusServerConfig config) {

    }

    public void start() throws IOException {
        accountingServer.start();
        authenticationServer.start();
    }

    public void shutdown() {
        authenticationServer.shutdown();
        accountingServer.shutdown();
    }

    class AuthenticationServer extends AbstractDatagramServer {
        public AuthenticationServer(int port, int threadSize) {
            super(port, threadSize, "RADIUS-Authentication-Server");
        }

        @Override
        protected boolean verifyPacket(ByteBuffer buffer) throws IOException {
            return false;
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {

        }

        @Override
        protected ByteBuffer createReceiveBuffer() {
            return null;
        }
    }

    class AccountingServer extends AbstractDatagramServer {

        public AccountingServer(int port, int threadSize) {
            super(port, threadSize, "RADIUS-Accounting-Server");
        }

        @Override
        protected boolean verifyPacket(ByteBuffer buffer) throws IOException {
            return false;
        }

        @Override
        protected void handlePacket(ByteBuffer buffer, SocketAddress remote) {

        }

        @Override
        protected ByteBuffer createReceiveBuffer() {
            return null;
        }
    }
}
