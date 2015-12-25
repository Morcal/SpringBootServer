package cn.com.xinli.portal.protocol.support;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.PortalClient;
import cn.com.xinli.portal.protocol.PortalProtocolException;
import cn.com.xinli.portal.protocol.Protocol;
import cn.com.xinli.portal.protocol.huawei.DefaultPortalClient;
import cn.com.xinli.portal.protocol.huawei.V1;
import cn.com.xinli.portal.protocol.huawei.V2;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * PWS portal client factory.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class PortalClients {
    /** Supported protocols. */
    static Set<Protocol> supportedProtocols = new HashSet<>();

    static {
        supportedProtocols.add(new V1());
        supportedProtocols.add(new V2());
    }

    /** Sole constructor. */
    private PortalClients() {
    }

    /**
     * Create portal client for given {@link Nas}.
     * @param nas nas/bras configuration.
     * @return portal client.
     */
    public static PortalClient create(Nas nas) {
        Optional<Protocol> protocol = supportedProtocols.stream()
                .filter(proto ->
                        Stream.of(proto.getSupportedNasTypeName())
                                .filter(name -> name.equalsIgnoreCase(nas.getType()))
                                .findAny()
                                .isPresent())
                .findAny();

        protocol.orElseThrow(() -> new PortalProtocolException("Unsupported nas type: " + nas.getType()));

        return new DefaultPortalClient(nas, protocol.get());
    }
}
