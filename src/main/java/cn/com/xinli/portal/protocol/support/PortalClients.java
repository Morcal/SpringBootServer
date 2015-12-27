package cn.com.xinli.portal.protocol.support;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.PortalClient;
import cn.com.xinli.portal.protocol.Protocol;
import cn.com.xinli.portal.protocol.UnsupportedNasException;
import cn.com.xinli.portal.protocol.UnsupportedProtocolException;
import cn.com.xinli.portal.protocol.huawei.DefaultPortalClient;
import cn.com.xinli.portal.protocol.huawei.V1;
import cn.com.xinli.portal.protocol.huawei.V2;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PWS portal client factory.
 * <p>
 * {@link #supportedProtocols} are "Flyweight" protocols, and shared
 * by associated {@link PortalClient}s.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public final class PortalClients {
    /**
     * Supported protocols.
     */
    static Set<Protocol> supportedProtocols = new HashSet<>();

    static {
        supportedProtocols.add(new V1());
        supportedProtocols.add(new V2());
    }

    /**
     * Sole constructor.
     */
    private PortalClients() {
    }

    /**
     * Create portal client for given {@link Nas}.
     *
     * @param nas nas/bras configuration.
     * @return portal client.
     */
    public static PortalClient create(Nas nas) {
        Optional<Protocol> protocol = supportedProtocols.stream()
                .filter(proto ->
                        Stream.of(proto.getSupportedNasTypes())
                                .filter(type -> type == nas.getType())
                                .findAny()
                                .isPresent())
                .findAny();

        protocol.orElseThrow(() -> new UnsupportedNasException(nas.getType().name()));

        switch (nas.getType()) {
            case HuaweiV1:
            case HuaweiV2:
                return new DefaultPortalClient(nas, protocol.get());

            default:
                break;
        }

        throw new UnsupportedProtocolException(nas.getType().name());
    }
}
