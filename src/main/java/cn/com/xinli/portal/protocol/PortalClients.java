package cn.com.xinli.portal.protocol;

import cn.com.xinli.portal.Nas;
import cn.com.xinli.portal.protocol.huawei.DefaultPortalClient;
import cn.com.xinli.portal.protocol.huawei.HuaweiCodecFactory;
import cn.com.xinli.portal.protocol.huawei.V1;
import cn.com.xinli.portal.protocol.huawei.V2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * PWS portal client factory.
 *
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class PortalClients {
    /** Supported protocols. */
    static Collection<Protocol> supportedProtocols = new ArrayList<>();

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
                .filter(proto -> proto.getSupportedTypeName().equalsIgnoreCase(nas.getType()))
                .findFirst();

        protocol.orElseThrow(() -> new PortalProtocolException("Unsupported nas type: " + nas.getType()));

        return new DefaultPortalClient(nas, new HuaweiCodecFactory(protocol.get().getVersion()));
    }
}
