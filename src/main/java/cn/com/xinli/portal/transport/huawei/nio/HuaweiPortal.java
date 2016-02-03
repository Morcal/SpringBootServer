package cn.com.xinli.portal.transport.huawei.nio;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.support.HuaweiPortalSessionProvider;
import cn.com.xinli.portal.transport.PortalClient;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.huawei.ClientHandler;
import cn.com.xinli.portal.transport.huawei.Endpoint;
import cn.com.xinli.portal.transport.huawei.ServerHandler;

import java.util.Objects;

/**
 * HUAWEI portal protocol facade.
 *
 * <p>PWS portal server factory.
 * Create portal server from given configuration.
 *
 * <p>PWS NAS server factory.
 * Create mock-huawei-nas device which supports huawei portal protocols.
 *
 * <p>PWS portal client factory.
 * {@link #codecFactory} is "Flyweight", and shared
 * by associated {@link PortalClient}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public class HuaweiPortal {
    /** Shared default handler. */
    private static final ClientHandler<HuaweiPacket> defaultHandler =
            new DefaultClientHandler();

    /** Shared codec factory. */
    private static final HuaweiCodecFactory codecFactory = new HuaweiCodecFactory();

    /** Sole constructor. */
    private HuaweiPortal() {
    }

    /**
     * Create a HUAWEI portal server.
     * @param endpoint portal endpoint.
     * @param handler portal server handler.
     * @return portal server.
     */
    public static PortalServer createServer(Endpoint endpoint, ServerHandler handler) {
        return new HuaweiPortalServer(endpoint, handler);
    }

    /**
     * Create a HUAWEI NAS which supports HUAWEI portal protocol(s).
     * @param endpoint huawei portal endpoint.
     * @return portal server.
     */
    public static PortalServer createNas(Endpoint endpoint) {
        return new HuaweiNas(endpoint);
    }

    /**
     * Create portal client for given {@link Nas}.
     *
     * @param endpoint huawei portal endpoint.
     * @return portal client.
     */
    public static PortalClient createClient(Endpoint endpoint)
            throws TransportException {
        Objects.requireNonNull(endpoint);
        return new DefaultPortalClient(endpoint, codecFactory, defaultHandler);
    }

    public static HuaweiPortalSessionProvider createSessionProvider() {
        return new HuaweiPortalSessionProvider();
    }
}
