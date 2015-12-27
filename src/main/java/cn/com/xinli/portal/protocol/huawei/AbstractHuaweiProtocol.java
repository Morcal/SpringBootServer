package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Protocol;

/**
 * Abstract huawei portal protocol.
 * <p>
 * This class creates a shared single {@link HuaweiCodecFactory}.
 * <p>
 * Project: xpws
 *
 * @see HuaweiCodecFactory
 * @author zhoupeng 2015/12/27.
 */
public abstract class AbstractHuaweiProtocol implements Protocol<HuaweiPacket> {
    /** Shared single codec factory. */
    protected static final HuaweiCodecFactory codecFactory = new HuaweiCodecFactory();

    @Override
    public Class<HuaweiPacket> getSupportedPacketClass() {
        return HuaweiPacket.class;
    }

    @Override
    public CodecFactory<HuaweiPacket> getCodecFactory() {
        return codecFactory;
    }
}
