package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.NasType;

/**
 * Huawei portal protocol version 2.
 * <p>
 * Protocol instances share a single {@link HuaweiCodecFactory}.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/2.
 * @see HuaweiCodecFactory
 */
public class V2 extends AbstractHuaweiProtocol {
    @Override
    public int getVersion() {
        return Enums.Version.v2.value();
    }

    @Override
    public NasType[] getSupportedNasTypes() {
        return new NasType[] { NasType.HuaweiV2 };
    }
}
