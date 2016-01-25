package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.transport.CodecFactory;
import cn.com.xinli.portal.transport.Protocol;

/**
 * Abstract huawei portal protocol.
 *
 * <p>This class creates a shared single {@link HuaweiCodecFactory}.
 *
 * <p>Project: xpws
 *
 * @see HuaweiCodecFactory
 * @author zhoupeng 2015/12/27.
 */
abstract class AbstractHuaweiProtocol implements Protocol<HuaweiPacket> {
    /** Shared single codec factory. */
    protected static final HuaweiCodecFactory codecFactory = new HuaweiCodecFactory();

    @Override
    public CodecFactory<HuaweiPacket> getCodecFactory() {
        return codecFactory;
    }
}
