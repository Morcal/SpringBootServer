package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.Nas;
import cn.com.xinli.portal.transport.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Huawei portal protocol facade.
 *
 * <p>PWS portal server factory.
 * Create portal server from given configuration.
 *
 * <p>PWS NAS server factory.
 * Create mock-huawei-nas device which supports huawei portal protocols.
 *
 * <p>PWS portal client factory.
 * {@link #supportedProtocols} are "Flyweight" protocols, and shared
 * by associated {@link PortalClient}s.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/1/4.
 */
public class HuaweiPortal {
    /** Supported protocols. */
    static Set<Protocol> supportedProtocols = new HashSet<>();

    static {
        supportedProtocols.add(new V1());
        supportedProtocols.add(new V2());
    }

    /** Sole constructor. */
    private HuaweiPortal() {
    }

    /**
     * Create a Huawei portal server.
     * @param config portal server config.
     * @param handler portal server handler.
     * @return portal server.
     */
    public static PortalServer createServer(PortalServerConfig config, PortalServerHandler handler) {
        return new HuaweiPortalServer(config, handler);
    }

    /**
     * Create a Huawei NAS which supports Huawei portal protocol(s).
     * @param nas NAS.
     * @return portal server.
     */
    public static PortalServer createNas(Nas nas) {
        return new HuaweiNas(nas);
    }

    /**
     * Create portal client for given {@link Nas}.
     *
     * @param nas nas/bras configuration.
     * @return portal client.
     */
    public static PortalClient createClient(Nas nas) throws PortalProtocolException {
        Optional<Protocol> protocol = supportedProtocols.stream()
                .filter(proto ->
                        Stream.of(proto.getSupportedNasTypes())
                                .filter(type -> type == nas.getType())
                                .findAny()
                                .isPresent())
                .findAny();

        protocol.orElseThrow(() -> new UnsupportedNasExceptionPortal(nas.getType()));

        switch (nas.getType()) {
            case HuaweiV1:
            case HuaweiV2:
                return new DefaultPortalClient(nas, protocol.get());

            default:
                break;
        }

        throw new UnsupportedPortalProtocolException(nas.getType().name());
    }
}
