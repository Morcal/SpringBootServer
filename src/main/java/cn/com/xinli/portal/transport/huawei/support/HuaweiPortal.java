package cn.com.xinli.portal.transport.huawei.support;

import cn.com.xinli.portal.core.nas.Nas;
import cn.com.xinli.portal.transport.Connector;
import cn.com.xinli.portal.transport.PortalServer;
import cn.com.xinli.portal.transport.TransportException;
import cn.com.xinli.portal.transport.huawei.*;
import cn.com.xinli.portal.transport.huawei.nio.ByteBufferCodecFactory;
import cn.com.xinli.portal.transport.huawei.nio.DatagramConnector;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

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
 * {@link #codecFactory} is "Flyweight", and shared by associated {@link Connector}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public class HuaweiPortal {
    /** Shared default handler. */
    private static final ConnectorHandler defaultHandler = new DefaultConnectorHandler();

    /** Shared codec factory. */
    private static final ByteBufferCodecFactory codecFactory = new ByteBufferCodecFactory();

    /** Connectors. */
    private static final Map<Endpoint, Connector<RequestContext>> connectors = new ConcurrentHashMap<>();

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
    public static Connector<RequestContext> getConnector(Endpoint endpoint)
            throws TransportException {
        Objects.requireNonNull(endpoint, Endpoint.EMPTY_ENDPOINT);

        if (!connectors.containsKey(endpoint)) {
            DatagramConnector connector = new DatagramConnector(endpoint, codecFactory, defaultHandler);
            connectors.putIfAbsent(endpoint, connector);
        }

        return connectors.get(endpoint);
    }
}
