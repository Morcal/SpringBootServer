package cn.com.xinli.portal.transport.huawei;

import cn.com.xinli.portal.core.NasType;

/**
 * Huawei portal protocol version 1.
 *
 * <p>Protocol instances share a single {@link HuaweiCodecFactory}.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 * @see cn.com.xinli.portal.transport.huawei.HuaweiCodecFactory
 */
final class V1 extends AbstractHuaweiProtocol {
    @Override
    public int getVersion() {
        return Version.V1.value();
    }

    @Override
    public NasType[] getSupportedNasTypes() {
        return new NasType[] { NasType.HuaweiV1 };
    }
}
